package com.inovex.inoventory.notification.dto

import com.inovex.inoventory.product.dto.EAN
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDate

class NotificationTest {
    @Test
    fun `creates ID correctly`() {
        val listId = 99L
        val date = LocalDate.of(2025,2,22)
        val notificationQueryResult = NotificationQueryResult(date, 3)

        val dto = Notification.fromEntity(notificationQueryResult, listId)

        assertEquals(dto.id, 2025022299) // <yyyyMMdd<listID>>
        assertEquals(dto.count, 3)
        assertEquals(dto.date, date)
    }

}