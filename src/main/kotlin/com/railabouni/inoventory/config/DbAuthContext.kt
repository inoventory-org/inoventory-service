package com.railabouni.inoventory.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Profile
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Component

interface DbAuthContext {
    fun apply()
}

@Component
@Profile("!local")
class SupabaseDbAuthContext(
    private val jdbcTemplate: JdbcTemplate,
    private val objectMapper: ObjectMapper
) : DbAuthContext {
    override fun apply() {
        val auth = SecurityContextHolder.getContext().authentication as? JwtAuthenticationToken ?: return
        val claims = auth.token.claims
        val claimsJson = objectMapper.writeValueAsString(claims)
        val role = (claims["role"] as? String) ?: "authenticated"

        jdbcTemplate.queryForObject(
            "select set_config('request.jwt.claims', ?, true)",
            String::class.java,
            claimsJson
        )
        jdbcTemplate.queryForObject(
            "select set_config('role', ?, true)",
            String::class.java,
            role
        )
    }
}

@Component
@Profile("local")
class NoopDbAuthContext : DbAuthContext {
    override fun apply() = Unit
}
