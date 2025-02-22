package com.inovex.inoventory.notification.dto

import com.inovex.inoventory.notification.entity.NotificationEntity
import java.time.LocalDate
import java.time.format.DateTimeFormatter


data class NotificationQueryResult(
    val date: LocalDate,
    val count: Long
)
