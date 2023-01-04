package com.inovex.inoventory.list.dto

import com.inovex.inoventory.list.entity.InventoryListEntity
import com.inovex.inoventory.user.dto.UserDto

data class InventoryList(
    val id: Long? = -1,
    val name: String,
    val user: UserDto? = null
) {
    fun toEntity(): InventoryListEntity {
        require(user != null)
        return InventoryListEntity(
            id = id,
            name = name,
            user = user.toEntity()
        )
    }

    companion object {
        fun fromEntity(list: InventoryListEntity) = InventoryList(
            id = list.id,
            name = list.name,
            user = UserDto.fromEntity(list.user)
        )
    }
}