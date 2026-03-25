package com.inovex.inoventory.user.service

import com.inovex.inoventory.user.dto.UserDto
import org.springframework.stereotype.Service

@Service
class CurrentUserService(
    private val userDetailsExtractor: UserDetailsExtractor
) {
    fun getCurrentUser(): UserDto = userDetailsExtractor.extractUser()
}
