package com.railabouni.inoventory.product

import com.railabouni.inoventory.ean.api.EanApiConnector
import com.railabouni.inoventory.product.dto.EAN
import com.railabouni.inoventory.product.dto.Product
import com.railabouni.inoventory.product.search.SearchCriteria
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service

@Service
class ProductService(
    private val apiConnector: EanApiConnector,
    private val cache: ProductMemoryCache
) {

    fun findAll(searchCriteria: List<SearchCriteria> = listOf()): List<Product> {
        if (searchCriteria.isEmpty()) {
            return listOf()
        }
        val results = runBlocking { apiConnector.search(searchCriteria) }
        cache.putAll(results)
        return results
    }

    fun scan(ean: EAN, fresh: Boolean = false): Product? {
        if (!fresh) {
            cache.get(ean.value)?.let { return it }
        }
        val product = runBlocking { apiConnector.findByEan(ean) }
        product?.let { cache.put(it) }
        return product
    }

    fun cacheProduct(product: Product): Product {
        cache.put(product)
        return product
    }
}
