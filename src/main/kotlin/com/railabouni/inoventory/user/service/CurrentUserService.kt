package com.railabouni.inoventory.user.service

import com.railabouni.inoventory.user.dto.UserDto
import org.springframework.stereotype.Service

@Service
class CurrentUserService(
    private val userDetailsExtractor: UserDetailsExtractor
) {
    fun getCurrentUser(): UserDto = userDetailsExtractor.extractUser()
}
