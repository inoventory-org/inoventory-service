package com.inovex.inoventory.user.dto

import com.inovex.inoventory.user.domain.User
import java.util.*

data class UserDto(
    val id: UUID,
    val userName: String
) {
    fun toDomain() = User(
        id = id,
        userName = userName
    )

    companion object {
        fun fromDomain(user: User) = UserDto(
            id = user.id,
            userName = user.userName
        )
    }
}