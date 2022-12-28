package com.inovex.inoventory.user.service

import com.inovex.inoventory.user.UserRepository
import com.inovex.inoventory.user.domain.User
import org.springframework.stereotype.Service

@Service
class FakeUserService(private val userRepository: UserRepository)  : UserService {
    override fun getAuthenticatedUser(): User? = userRepository.findAll().firstOrNull()
}