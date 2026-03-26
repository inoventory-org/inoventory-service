package com.railabouni.inoventory.list

import com.railabouni.inoventory.list.entity.InventoryListEntity
import org.springframework.data.jpa.repository.JpaRepository

interface InventoryListRepository : JpaRepository<InventoryListEntity, Long> {
    fun findAllByIdIn(ids: List<Long>) : List<InventoryListEntity>
}