package com.railabouni.inoventory.product

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties(prefix = "product.cache")
data class ProductCacheProperties(
    val ttl: Duration = Duration.ofMinutes(5),
    val maxSize: Int = 100
)
