package com.inovex.inoventory.user.service

import com.inovex.inoventory.user.dto.UserDto
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtUserExtractor : UserDetailsExtractor {
    override fun extractUser(): UserDto {
        val claims = (SecurityContextHolder.getContext().authentication as JwtAuthenticationToken).token.claims
        return UserDto(
            id = UUID.fromString(claims["sub"].toString()),
            userName = claims["preferred_username"].toString()
        )
    }
}
