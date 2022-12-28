package com.inovex.inoventory.user

import com.inovex.inoventory.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun findByUserName(userName: String) : User?
}