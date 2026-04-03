package com.railabouni.inoventory.notification

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.railabouni.inoventory.list.entity.InventoryListEntity
import com.railabouni.inoventory.list.item.ListItemRepository
import com.railabouni.inoventory.list.item.entity.ListItemEntity
import com.railabouni.inoventory.user.fcm.UserFcmTokenService
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.UUID

class NotificationWebhookServiceTest {

    private val listItemRepository: ListItemRepository = mockk()
    private val fcmTokenService: UserFcmTokenService = mockk()
    private val service = NotificationWebhookService(listItemRepository, fcmTokenService)

    @Test
    fun `triggerExpirationNotifications should query items and send FCM`() {
        // Setup Mocks
        val userId = UUID.randomUUID()
        val listId = 100L
        val listEntity = InventoryListEntity(id = listId, name = "Groceries", userId = userId)
        val expiringItem1 = ListItemEntity(
            id = 1,
            expirationDate = LocalDate.now().plusDays(5),
            productEan = "123",
            productName = "Milk",
            list = listEntity,
            notificationSent = false
        )
        val expiringItem2 = ListItemEntity(
            id = 2,
            expirationDate = LocalDate.now().plusDays(10),
            productEan = "456",
            productName = "Eggs",
            list = listEntity,
            notificationSent = false
        )

        every { listItemRepository.findByExpirationDateBeforeAndNotificationSentFalse(any()) } returns listOf(expiringItem1, expiringItem2)
        every { fcmTokenService.getToken(userId) } returns "mock-fcm-token"
        
        // Mock Firebase Messaging Static Call
        mockkStatic(FirebaseMessaging::class)
        val mockFirebaseMessaging = mockk<FirebaseMessaging>()
        every { FirebaseMessaging.getInstance() } returns mockFirebaseMessaging
        every { mockFirebaseMessaging.send(any()) } returns "message_id"

        every { listItemRepository.saveAll(any<List<ListItemEntity>>()) } answers { firstArg() }

        // Execute
        val successCount = service.triggerExpirationNotifications()

        // Verify
        assertEquals(1, successCount) // 1 list processed
        
        verify {
            mockFirebaseMessaging.send(withArg<Message> { message ->
                // It's tricky to assert on a final java class like Message cleanly without reflection
                // We'll just verify send was called
            })
            listItemRepository.saveAll(withArg<List<ListItemEntity>> { items ->
                assertEquals(2, items.size)
                assertEquals(true, items[0].notificationSent)
                assertEquals(true, items[1].notificationSent)
            })
        }
    }
}
