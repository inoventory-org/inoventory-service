package com.inovex.inoventory.notification

import com.inovex.inoventory.notification.dto.NotificationQueryResult
import com.inovex.inoventory.notification.entity.NotificationEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query


interface NotificationRepository : JpaRepository<NotificationEntity, Long> {
    @Query(
        """
    SELECT NEW com.inovex.inoventory.notification.dto.NotificationQueryResult(n.date, COUNT(n)) 
    FROM NotificationEntity n 
    JOIN ListItemEntity i ON n.item.id = i.id 
    WHERE i.list.id = :listId 
    GROUP BY n.date
"""
    )
    fun findGroupedNotificationsByListId(listId: Long): List<NotificationQueryResult>

}

