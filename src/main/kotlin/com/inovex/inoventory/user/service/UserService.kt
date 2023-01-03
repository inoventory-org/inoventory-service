package com.inovex.inoventory.user.service

import com.inovex.inoventory.user.UserRepository
import com.inovex.inoventory.user.dto.UserDto
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class UserService(
    private val repository: UserRepository,
    private val userDetailsExtractor: UserDetailsExtractor
) {
    fun getAuthenticatedUser(): UserDto {
        val userDto = userDetailsExtractor.extractUser()
        return UserDto.fromDomain(
            repository.findByIdOrNull(userDto.id)
                ?: repository.saveAndFlush(userDto.toDomain())
        )
    }
}