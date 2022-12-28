package com.inovex.inoventory.config

import com.inovex.inoventory.user.UserRepository
import com.inovex.inoventory.user.domain.User
import io.ktor.util.logging.*
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
class DbConfig {

    @Bean
    @Profile("!test")
    fun initDb(userRepository: UserRepository) = CommandLineRunner {
        val testUserName = "luke.skywalker"
        val user = userRepository.findByUserName(testUserName)
        if (user == null) {
            userRepository.save(User(userName = "luke.skywalker"))
        }
    }

}