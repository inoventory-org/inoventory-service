package com.railabouni.inoventory.config

import com.railabouni.inoventory.product.ProductCacheProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling

@Configuration
@EnableScheduling
@EnableConfigurationProperties(ProductCacheProperties::class)
class AppConfig
