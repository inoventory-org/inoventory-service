package com.inovex.inoventory.user.service

import com.inovex.inoventory.user.dto.UserDto
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.util.*

@Component
@Profile("local")
class MockUserExtractor(
) : UserDetailsExtractor {

    override fun extractUser() = TEST_USER

    companion object {
        private const val USER_ID = "328d58cc-1973-4e53-99d4-f49e8b48dc60"

        val TEST_USER = UserDto(id = UUID.fromString(USER_ID), userName = "luke.skywalker")
    }
}