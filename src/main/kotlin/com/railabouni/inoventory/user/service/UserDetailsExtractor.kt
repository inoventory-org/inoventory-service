package com.railabouni.inoventory.user.service

import com.railabouni.inoventory.user.dto.UserDto

interface UserDetailsExtractor {
    fun extractUser(): UserDto
}