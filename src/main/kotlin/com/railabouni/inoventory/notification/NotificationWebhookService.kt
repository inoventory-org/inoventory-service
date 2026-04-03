package com.railabouni.inoventory.notification

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import com.railabouni.inoventory.list.item.ListItemRepository
import com.railabouni.inoventory.user.fcm.UserFcmTokenService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class NotificationWebhookService(
    private val listItemRepository: ListItemRepository,
    private val fcmTokenService: UserFcmTokenService
) {

    @Transactional
    fun triggerExpirationNotifications(): Int {
        val expirationThreshold = LocalDate.now().plusMonths(1)
        val expiringItems = listItemRepository.findByExpirationDateBeforeAndNotificationSentFalse(expirationThreshold)
        
        if (expiringItems.isEmpty()) {
            return 0
        }

        // Group items by List Id so we can send 1 notification per list
        val itemsByList = expiringItems.groupBy { it.list }

        var successCount = 0

        itemsByList.forEach { (inventoryList, items) ->
            val userId = inventoryList.userId
            val fcmToken = fcmTokenService.getToken(userId)

            if (fcmToken != null) {
                // Prepare Firebase Message
                val notificationTitle = "Items Expiring Soon"
                val notificationBody = "You have ${items.size} item(s) expiring soon in list: ${inventoryList.name}"

                val message = Message.builder()
                    .setToken(fcmToken)
                    .setNotification(
                        Notification.builder()
                            .setTitle(notificationTitle)
                            .setBody(notificationBody)
                            .build()
                    )
                    .putData("listId", inventoryList.id.toString())
                    .build()

                try {
                    FirebaseMessaging.getInstance().send(message)
                    // Update flags
                    items.forEach { it.notificationSent = true }
                    listItemRepository.saveAll(items)
                    successCount++
                } catch (e: Exception) {
                    println("Failed to send FCM to user $userId: ${e.message}")
                }
            }
        }
        return successCount
    }
}
