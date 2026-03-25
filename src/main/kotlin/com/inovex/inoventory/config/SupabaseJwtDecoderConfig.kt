package com.inovex.inoventory.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.oauth2.core.OAuth2Error
import org.springframework.security.oauth2.core.OAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtValidators
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder

@Configuration
@Profile("!local & !test")
class SupabaseJwtDecoderConfig(
    @Value("\${spring.security.oauth2.resourceserver.jwt.issuer-uri}") private val issuer: String,
    @Value("\${supabase.jwt.audience:authenticated}") private val audience: String
) {
    @Bean
    fun jwtDecoder(): JwtDecoder {
        val decoder = NimbusJwtDecoder.withIssuerLocation(issuer).build()
        val withIssuer = JwtValidators.createDefaultWithIssuer(issuer)
        val withAudience = OAuth2TokenValidator<Jwt> { jwt ->
            val audiences = jwt.audience
            if (audiences.contains(audience)) {
                OAuth2TokenValidatorResult.success()
            } else {
                OAuth2TokenValidatorResult.failure(OAuth2Error("invalid_token", "Invalid audience", null))
            }
        }
        decoder.setJwtValidator(DelegatingOAuth2TokenValidator(withIssuer, withAudience))
        return decoder
    }
}
