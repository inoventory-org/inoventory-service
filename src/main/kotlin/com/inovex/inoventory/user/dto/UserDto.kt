package com.inovex.inoventory.user.dto

import com.inovex.inoventory.user.entity.UserEntity
import java.util.*

data class UserDto(
    val id: UUID,
    val userName: String
) {
    fun toEntity() = UserEntity(
        id = id,
        userName = userName
    )

    companion object {
        fun fromEntity(user: UserEntity) = UserDto(
            id = user.id,
            userName = user.userName
        )
    }
}