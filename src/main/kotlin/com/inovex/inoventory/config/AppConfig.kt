package com.inovex.inoventory.config

import com.inovex.inoventory.product.ProductCacheProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling

@Configuration
@EnableScheduling
@EnableConfigurationProperties(ProductCacheProperties::class)
class AppConfig
