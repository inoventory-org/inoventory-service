package com.inovex.inoventory.config

import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Profile
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

@Component
@Profile("!test")
class RequiredEnvValidator(
    private val environment: Environment
) : ApplicationRunner {
    private val log = LoggerFactory.getLogger(RequiredEnvValidator::class.java)

    override fun run(args: ApplicationArguments) {
        val required = environment.getProperty("app.required-env")
            ?.split(",")
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() }
            ?: emptyList()

        if (required.isEmpty()) return

        val missing = required.filter { key ->
            val envValue = System.getenv(key)
            val propValue = environment.getProperty(key)
            envValue.isNullOrBlank() && propValue.isNullOrBlank()
        }

        if (missing.isNotEmpty()) {
            val message = "Missing required environment variables: ${missing.joinToString(", ")}"
            log.error(message)
            throw IllegalStateException(message)
        }
    }
}
