package com.inovex.inoventory.product

import com.inovex.inoventory.openfoodfacts.EanConnector
import com.inovex.inoventory.openfoodfacts.ProductsConnector
import com.inovex.inoventory.product.dto.EAN
import com.inovex.inoventory.product.dto.Product
import com.inovex.inoventory.product.entity.SourceEntity
import com.inovex.inoventory.product.search.ProductSpecification
import com.inovex.inoventory.product.search.SearchCriteria
import kotlinx.coroutines.runBlocking
import org.springframework.data.jpa.domain.Specification
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class ProductService(private val repository: ProductRepository, private val apiConnector: EanConnector, private val productsConnector: ProductsConnector) {

    @Scheduled(cron = "0 0 0 * * *")
    private fun updateProductCache() =
        repository.findByCachedTimestampBefore(Instant.now().minus(1, ChronoUnit.DAYS))
            .forEach { findAndCacheApiProduct(EAN(it.ean)) }

    fun findAll(searchCriteria: List<SearchCriteria> = listOf()): List<Product> {
        val specification = Specification.allOf(searchCriteria.map { ProductSpecification(it) })
        val localResults = repository.findAll(specification).map { Product.fromEntity(it) }
        if (localResults.any())
            return localResults

        return findAndCacheApiProducts(searchCriteria)
    }

    fun scan(ean: EAN, fresh: Boolean = false) : Product? {
        if (fresh) {
            return findAndCacheApiProduct(ean)
        }
        return repository.findByEan(ean.value)?.let { Product.fromEntity(it) } ?: findAndCacheApiProduct(ean)
    }

    fun upsert(product: Product, source: SourceEntity): Product =
        Product.fromEntity(repository.save(product.toEntity(source, Instant.now())))

    private fun findAndCacheApiProduct(ean: EAN) =
        runBlocking { apiConnector.findByEan(ean) }?.let { upsert(it, SourceEntity.API) }

    private fun findAndCacheApiProducts(searchCriteria: List<SearchCriteria>) =
        runBlocking { apiConnector.search(searchCriteria) }.map { upsert(it, SourceEntity.API) }
}