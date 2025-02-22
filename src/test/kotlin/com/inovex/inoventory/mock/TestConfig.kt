package com.inovex.inoventory.mock

import com.inovex.inoventory.user.service.MockUserExtractor
import com.inovex.inoventory.user.service.UserDetailsExtractor
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.security.oauth2.jwt.JwtDecoder


@TestConfiguration
class TestConfig {

    @MockBean
    private val jwtDecoder: JwtDecoder? = null

    @Bean
    @Primary
    fun configureUserExtractor(): UserDetailsExtractor = MockUserExtractor()
}