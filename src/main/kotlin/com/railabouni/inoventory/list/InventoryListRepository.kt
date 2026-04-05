package com.railabouni.inoventory.list

import com.railabouni.inoventory.list.entity.InventoryListEntity
import com.railabouni.inoventory.list.entity.InventoryListType
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface InventoryListRepository : JpaRepository<InventoryListEntity, Long> {
    fun findAllByIdIn(ids: List<Long>) : List<InventoryListEntity>
    fun findByUserIdAndType(userId: UUID, type: InventoryListType): InventoryListEntity?
    fun findAllByUserIdOrderBySortOrderAscIdAsc(userId: UUID): List<InventoryListEntity>
}
