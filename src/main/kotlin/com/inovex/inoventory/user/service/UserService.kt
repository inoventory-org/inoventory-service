package com.inovex.inoventory.user.service

import com.inovex.inoventory.user.UserRepository
import com.inovex.inoventory.user.dto.UserDto
import com.inovex.inoventory.config.DbAuthContext
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val repository: UserRepository,
    private val userDetailsExtractor: UserDetailsExtractor,
    private val dbAuthContext: DbAuthContext
) {
    @Transactional
    fun getAuthenticatedUser(): UserDto {
        dbAuthContext.apply()
        val userDto = userDetailsExtractor.extractUser()
        return UserDto.fromEntity(
            repository.findByIdOrNull(userDto.id)
                ?: repository.saveAndFlush(userDto.toEntity())
        )
    }
}
