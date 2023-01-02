package com.inovex.inoventory.user.service

import com.inovex.inoventory.user.UserRepository
import com.inovex.inoventory.user.domain.User
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class UserService(
    private val repository: UserRepository,
    private val jwtUserExtractor: JwtUserExtractor
) {
    fun getAuthenticatedUser(): User {
        val user = jwtUserExtractor.extractUser()
        return repository.findByIdOrNull(user.id) ?: repository.saveAndFlush(user)
    }
}