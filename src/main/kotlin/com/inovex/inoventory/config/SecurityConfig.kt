package com.inovex.inoventory.config

import com.c4_soft.springaddons.security.oauth2.config.synchronised.ExpressionInterceptUrlRegistryPostProcessor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity


@Configuration
@EnableMethodSecurity
class SecurityConfig {
    @Bean
    fun expressionInterceptUrlRegistryPostProcessor(): ExpressionInterceptUrlRegistryPostProcessor {
        return ExpressionInterceptUrlRegistryPostProcessor {
            it.requestMatchers("/**").hasAuthority("inoventory-user")
                .anyRequest().authenticated()
        }
    }
}