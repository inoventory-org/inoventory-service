package com.inovex.inoventory.config

import org.springframework.boot.SpringApplication
import org.springframework.boot.env.EnvironmentPostProcessor
import org.springframework.core.Ordered
import org.springframework.core.env.ConfigurableEnvironment

class EnvDiagnosticsPostProcessor : EnvironmentPostProcessor, Ordered {
    override fun postProcessEnvironment(
        environment: ConfigurableEnvironment,
        application: SpringApplication,
    ) {
        val dbUrl = environment.getProperty("spring.datasource.url") ?: "<unset>"
        val dbUser = environment.getProperty("spring.datasource.username") ?: "<unset>"
        val dbPassword = environment.getProperty("spring.datasource.password")
        val maskedPassword = when {
            dbPassword.isNullOrEmpty() -> "<empty>"
            else -> "*".repeat(dbPassword.length.coerceAtMost(12))
        }
        val profiles = environment.activeProfiles.joinToString(",").ifEmpty { "<none>" }

        System.err.println("[EnvDiagnostics] profiles=$profiles")
        System.err.println("[EnvDiagnostics] spring.datasource.url=$dbUrl")
        System.err.println("[EnvDiagnostics] spring.datasource.username=$dbUser")
        System.err.println("[EnvDiagnostics] spring.datasource.password=$maskedPassword")
    }

    override fun getOrder(): Int = Ordered.HIGHEST_PRECEDENCE
}
