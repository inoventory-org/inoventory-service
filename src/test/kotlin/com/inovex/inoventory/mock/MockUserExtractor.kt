package com.inovex.inoventory.mock

import com.inovex.inoventory.user.dto.UserDto
import com.inovex.inoventory.user.service.UserDetailsExtractor

class MockUserExtractor(
    private val mockedUser: UserDto
) : UserDetailsExtractor {
    override fun extractUser() = mockedUser
}