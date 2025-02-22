package com.inovex.inoventory.notification.dto

import java.time.LocalDate
import java.time.format.DateTimeFormatter



data class Notification(
    val id: Int,
    val count: Long,
    val date: LocalDate,
) {
    companion object {
        fun fromEntity(res: NotificationQueryResult, listId: Long) = Notification(
            id = calculateID(res, listId),
            date = res.date,
            count = res.count,
        )

        private fun calculateID(res: NotificationQueryResult, listId: Long): Int {
            val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
            val formattedDate = res.date.format(formatter)
            val idStr = formattedDate+listId

            return idStr.toInt()
        }
    }
}