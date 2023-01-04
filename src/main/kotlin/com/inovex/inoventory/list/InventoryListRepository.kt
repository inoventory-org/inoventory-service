package com.inovex.inoventory.list

import com.inovex.inoventory.list.entity.InventoryListEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface InventoryListRepository : JpaRepository<InventoryListEntity, Long> {
    fun findByUserId(userId: UUID): List<InventoryListEntity>
}