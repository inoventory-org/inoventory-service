package com.inovex.inoventory.product

import com.inovex.inoventory.ean.api.EanApiConnector
import com.inovex.inoventory.product.entity.SourceEntity
import com.inovex.inoventory.product.dto.EAN
import com.inovex.inoventory.product.dto.Product
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service

@Service
class ProductService(private val repository: ProductRepository, private val apiConnector: EanApiConnector) {

    fun findAll(): List<Product> = repository.findAll().map { Product.fromEntity(it) }

    fun findOrNull(ean: EAN): Product? {
        return repository.findByEan(ean.value)?.let { Product.fromEntity(it) } ?: findAndCacheApiProduct(ean)
    }

    fun create(product: Product, source: SourceEntity): Product {
        return Product.fromEntity(repository.save(product.toEntity(source)))
    }

    private fun findAndCacheApiProduct(ean: EAN): Product? {
        return runBlocking { apiConnector.findByEan(ean) }?.let { create(it, SourceEntity.API) }
    }
}