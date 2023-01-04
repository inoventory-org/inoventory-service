package com.inovex.inoventory.list

import com.inovex.inoventory.list.entity.InventoryListEntity
import org.springframework.data.jpa.repository.JpaRepository

interface InventoryListRepository : JpaRepository<InventoryListEntity, Long> {
    fun findAllByIdIn(ids: List<Long>) : List<InventoryListEntity>
}