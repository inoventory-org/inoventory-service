package com.inovex.inoventory.list.item

import com.inovex.inoventory.list.item.entity.ListItemEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface ListItemRepository : JpaRepository<ListItemEntity, Long> {
    fun findAllByListId(listId: Long): List<ListItemEntity>
    fun findByExpirationDateBeforeAndNotificationSentFalse(expirationDate: LocalDate): List<ListItemEntity>
}