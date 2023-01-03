package com.inovex.inoventory.user.service

import com.inovex.inoventory.user.dto.UserDto

interface UserDetailsExtractor {
    fun extractUser(): UserDto
}