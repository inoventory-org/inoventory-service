package com.inovex.inoventory.user

import com.inovex.inoventory.user.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserRepository : JpaRepository<UserEntity, UUID> {
    fun findByUserName(userName: String): UserEntity?
}