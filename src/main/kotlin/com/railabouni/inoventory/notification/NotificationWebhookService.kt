package com.railabouni.inoventory.notification

import com.google.firebase.messaging.AndroidConfig
import com.google.firebase.messaging.AndroidNotification
import com.google.firebase.messaging.ApnsConfig
import com.google.firebase.messaging.Aps
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import com.railabouni.inoventory.list.item.ListItemRepository
import com.railabouni.inoventory.user.fcm.UserFcmTokenService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class NotificationWebhookService(
    private val listItemRepository: ListItemRepository,
    private val fcmTokenService: UserFcmTokenService
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    fun triggerExpirationNotifications(): Int {
        val threshold = LocalDate.now().plusMonths(1)
        val items = listItemRepository.findByExpirationDateBeforeAndNotificationSentFalse(threshold)
        
        logger.info("Found ${items.size} items expiring before $threshold")
        
        if (items.isEmpty()) return 0

        val itemsByList = items.groupBy { it.list.id }
        var successCount = 0

        itemsByList.forEach { (listId, listItems) ->
            val inventoryList = listItems.first().list
            val userId = inventoryList.userId
            
            val fcmToken = fcmTokenService.getToken(userId)

            if (fcmToken == null) {
                logger.warn("Skipping List $listId: No FCM token found for User $userId")
                return@forEach 
            }

            try {
                val message = Message.builder()
                    .setToken(fcmToken)
                    .setNotification(
                        Notification.builder()
                            .setTitle("Items Expiring Soon")
                            .setBody("You have ${listItems.size} item(s) expiring soon in: ${inventoryList.name}")
                            .build()
                    )
                    // Android Specifics
                    .setAndroidConfig(
                        AndroidConfig.builder()
                            .setPriority(AndroidConfig.Priority.HIGH) // Bypasses Doze mode
                            .setNotification(
                                AndroidNotification.builder()
                                    .setSound("default")
                                    .setClickAction("FLUTTER_NOTIFICATION_CLICK") // Required for some older versions
                                    .setChannelId("expiring_items_channel") // MUST match your Flutter setup
                                    .build()
                            )
                            .build()
                    )
                    // iOS Specifics
                    .setApnsConfig(
                        ApnsConfig.builder()
                            .putAllHeaders(mapOf("apns-priority" to "10")) // Priority 10 is "Immediate"
                            .setAps(
                                Aps.builder()
                                    .setSound("default")
                                    .setThreadId("expiring_items")
                                    .build()
                            )
                            .build()
                    )
                    .putData("listId", listId.toString())
                    .build()

                FirebaseMessaging.getInstance().send(message)
                
                listItems.forEach { it.notificationSent = true }
                listItemRepository.saveAll(listItems)
                
                successCount++
                logger.info("Notification sent successfully for List $listId to User $userId")
            } catch (e: Exception) {
                logger.error("Firebase failure for User $userId: ${e.message}")
            }
        }
        return successCount
    }
}