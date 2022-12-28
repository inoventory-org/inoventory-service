package com.inovex.inoventory.list.item

import com.inovex.inoventory.list.item.domain.ListItem
import org.springframework.data.jpa.repository.JpaRepository

interface ListItemRepository : JpaRepository<ListItem, Long> {
    fun findAllByListId(listId: Long): List<ListItem>
    fun findByIdAndListId(id: Long, listId: Long): ListItem?

}