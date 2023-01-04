package com.inovex.inoventory.product

import com.inovex.inoventory.ean.api.EanApiConnector
import com.inovex.inoventory.product.dto.EAN
import com.inovex.inoventory.product.dto.Product
import com.inovex.inoventory.product.entity.SourceEntity
import com.inovex.inoventory.product.search.ProductSpecification
import com.inovex.inoventory.product.search.SearchCriteria
import kotlinx.coroutines.runBlocking
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class ProductService(private val repository: ProductRepository, private val apiConnector: EanApiConnector) {

    fun findAll(searchCriteria: List<SearchCriteria> = listOf()): List<Product> {
        val specification = Specification.allOf(searchCriteria.map { ProductSpecification(it) })
        return repository.findAll(specification).map { Product.fromEntity(it) }
    }


    fun findOrNull(ean: EAN): Product? {
        return repository.findByEan(ean.value)?.let { Product.fromEntity(it) } ?: findAndCacheApiProduct(ean)
    }

    fun create(product: Product, source: SourceEntity): Product {
        return Product.fromEntity(repository.save(product.toEntity(source, Instant.now())))
    }

    private fun findAndCacheApiProduct(ean: EAN): Product? {
        return runBlocking { apiConnector.findByEan(ean) }?.let { create(it, SourceEntity.API) }
    }
}