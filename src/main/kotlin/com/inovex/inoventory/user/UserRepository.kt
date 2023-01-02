package com.inovex.inoventory.user

import com.inovex.inoventory.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserRepository : JpaRepository<User, UUID> {
    fun findByUserName(userName: String): User?
}