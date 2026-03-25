package com.inovex.inoventory.config

import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

@Component
class StartupDiagnostics(
    private val environment: Environment,
) : ApplicationRunner {
    private val log = LoggerFactory.getLogger(StartupDiagnostics::class.java)

    override fun run(args: ApplicationArguments) {
        val dbUrl = environment.getProperty("spring.datasource.url") ?: "<unset>"
        val dbUser = environment.getProperty("spring.datasource.username") ?: "<unset>"
        val dbPassword = environment.getProperty("spring.datasource.password")
        val maskedPassword = when {
            dbPassword.isNullOrEmpty() -> "<empty>"
            else -> "*".repeat(dbPassword.length.coerceAtMost(12))
        }

        log.info("DB config: url='{}', user='{}', password='{}'", dbUrl, dbUser, maskedPassword)
    }
}
