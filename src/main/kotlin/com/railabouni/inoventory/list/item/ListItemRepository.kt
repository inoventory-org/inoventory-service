package com.railabouni.inoventory.list.item

import com.railabouni.inoventory.list.item.entity.ListItemEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ListItemRepository : JpaRepository<ListItemEntity, Long> {
    fun findAllByListId(listId: Long): List<ListItemEntity>

}