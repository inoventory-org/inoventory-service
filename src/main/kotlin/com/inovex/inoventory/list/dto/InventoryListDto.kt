package com.inovex.inoventory.list.dto

import com.inovex.inoventory.list.domain.InventoryList
import com.inovex.inoventory.user.dto.UserDto

data class InventoryListDto(
    val id: Long? = -1,
    val name: String,
    val user: UserDto? = null
) {
    fun toDomain(): InventoryList {
        require(user != null)
        return InventoryList(
            id = id,
            name = name,
            user = user.toDomain()
        )
    }

    companion object {
        fun fromDomain(list: InventoryList) = InventoryListDto(
            id = list.id,
            name = list.name,
            user = UserDto.fromDomain(list.user)
        )
    }
}