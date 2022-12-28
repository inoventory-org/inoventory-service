package com.inovex.inoventory.user.service

import com.inovex.inoventory.user.domain.User

interface UserService {
    fun getAuthenticatedUser(): User?
}