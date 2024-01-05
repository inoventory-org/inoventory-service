package com.inovex.inoventory.config

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class HttpClientConfig {
    @Bean
    fun httpClient(): HttpClient =
        HttpClient(CIO) {
            expectSuccess = false
            install(ContentNegotiation) {
                json(
                    Json {
                        prettyPrint = true
                        encodeDefaults = true
                        ignoreUnknownKeys = true
                    }
                )
            }
            install(UserAgent) {
                agent = "inoventory/0.0.1 (eilabouni.rudy@gmail.com)"
            }
        }
}