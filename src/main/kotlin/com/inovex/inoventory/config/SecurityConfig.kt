package com.inovex.inoventory.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.web.SecurityFilterChain

@Configuration
@Profile("!local")
class SecurityConfig {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests {
                it.requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                    .anyRequest().authenticated()
            }
            .oauth2ResourceServer { oauth2 ->
                oauth2.jwt { it.jwtAuthenticationConverter(supabaseJwtConverter()) }
            }
            .csrf { it.disable() }

        return http.build()
    }

    @Bean
    fun supabaseJwtConverter(): JwtAuthenticationConverter {
        val converter = JwtAuthenticationConverter()

        converter.setJwtGrantedAuthoritiesConverter { jwt ->
            val authorities = mutableListOf<SimpleGrantedAuthority>()

            // Map the default Supabase "role" claim
            val defaultRole = jwt.claims["role"] as? String
            if (defaultRole != null) {
                authorities.add(SimpleGrantedAuthority("ROLE_${defaultRole.uppercase()}"))
            }

            // Map any custom roles stored in "app_metadata"
            val appMetadata = jwt.claims["app_metadata"] as? Map<*, *>
            val customRoles = appMetadata?.get("roles") as? List<*>

            customRoles?.forEach { role ->
                if (role is String) {
                    authorities.add(SimpleGrantedAuthority("ROLE_${role.uppercase()}"))
                }
            }

            authorities
        }

        return converter
    }
}

@Configuration
@Profile("local")
class SecurityConfigLocal {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests { it.anyRequest().permitAll() }
            .csrf { it.disable() }
        return http.build()
    }
}