package com.inovex.inoventory.mock

import com.inovex.inoventory.user.dto.UserDto
import com.inovex.inoventory.user.service.UserDetailsExtractor
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.security.oauth2.jwt.JwtDecoder
import java.util.*


@TestConfiguration
class TestConfig {

    @MockBean
    private val jwtDecoder: JwtDecoder? = null
    @Bean
    @Primary
    fun configureUserExtractor(): UserDetailsExtractor = MockUserExtractor(TEST_USER)

    companion object {
        private const val USER_ID = "328d58cc-1973-4e53-99d4-f49e8b48dc60"

        val TEST_USER = UserDto(id = UUID.fromString(USER_ID), userName = "luke.skywalker")
    }

}