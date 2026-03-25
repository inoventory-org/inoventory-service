package com.inovex.inoventory.user.service

import com.inovex.inoventory.user.dto.UserDto
import org.springframework.context.annotation.Profile
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Component
import java.util.*

@Component
@Profile("!local")
class JwtUserExtractor : UserDetailsExtractor {
    override fun extractUser(): UserDto {
        val claims = (SecurityContextHolder.getContext().authentication as JwtAuthenticationToken).token.claims
        val userMetadata = claims["user_metadata"] as? Map<*, *>
        val userName =
            (userMetadata?.get("user_name") as? String)
                ?: (userMetadata?.get("username") as? String)
                ?: (userMetadata?.get("name") as? String)
                ?: (claims["email"] as? String)
                ?: (claims["sub"] as? String)
                ?: "unknown"
        return UserDto(
            id = UUID.fromString(claims["sub"].toString()),
            userName = userName
        )
    }
}
