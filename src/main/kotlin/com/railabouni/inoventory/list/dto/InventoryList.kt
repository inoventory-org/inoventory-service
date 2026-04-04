package com.railabouni.inoventory.list.dto

import com.railabouni.inoventory.list.entity.InventoryListEntity
import java.util.UUID

data class InventoryList(
    val id: Long? = -1,
    val name: String,
    val userId: UUID? = null
) {
    fun toEntity(userId: UUID): InventoryListEntity {
        return InventoryListEntity(
            id = id,
            name = name,
            userId = userId
        )
    }

    companion object {
        fun fromEntity(list: InventoryListEntity) = InventoryList(
            id = list.id,
            name = list.name,
            userId = list.userId
        )
    }
}
