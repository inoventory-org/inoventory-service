package com.railabouni.inoventory.list.dto

import com.railabouni.inoventory.list.entity.InventoryListEntity
import com.railabouni.inoventory.list.entity.InventoryListType
import java.util.UUID

data class InventoryList(
    val id: Long? = -1,
    val name: String,
    val userId: UUID? = null,
    val type: InventoryListType = InventoryListType.REGULAR
) {
    fun toEntity(userId: UUID): InventoryListEntity {
        return InventoryListEntity(
            id = id,
            name = name,
            userId = userId,
            type = type
        )
    }

    companion object {
        fun fromEntity(list: InventoryListEntity) = InventoryList(
            id = list.id,
            name = list.name,
            userId = list.userId,
            type = list.type
        )
    }
}
