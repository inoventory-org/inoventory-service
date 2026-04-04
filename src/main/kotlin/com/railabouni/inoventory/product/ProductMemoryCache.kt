package com.railabouni.inoventory.product

import com.railabouni.inoventory.product.dto.Product
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class ProductMemoryCache(private val properties: ProductCacheProperties) {
    private data class CacheEntry(val product: Product, val expiresAt: Instant)

    private val lock = Any()
    private val cache = object : LinkedHashMap<String, CacheEntry>(16, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, CacheEntry>?): Boolean {
            return size > properties.maxSize
        }
    }

    fun get(ean: String): Product? = synchronized(lock) {
        val entry = cache[ean] ?: return null
        if (Instant.now().isAfter(entry.expiresAt)) {
            cache.remove(ean)
            return null
        }
        entry.product
    }

    fun put(product: Product) = synchronized(lock) {
        cache[product.ean.value] = CacheEntry(product, Instant.now().plus(properties.ttl))
    }

    fun putAll(products: List<Product>) = synchronized(lock) {
        val expiresAt = Instant.now().plus(properties.ttl)
        products.forEach { product ->
            cache[product.ean.value] = CacheEntry(product, expiresAt)
        }
    }
}
