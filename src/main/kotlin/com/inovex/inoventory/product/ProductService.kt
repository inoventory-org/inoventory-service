package com.inovex.inoventory.product

import com.inovex.inoventory.ean.EanApiConnector
import com.inovex.inoventory.product.domain.Source
import com.inovex.inoventory.product.dto.EAN
import com.inovex.inoventory.product.dto.ProductDto
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service

@Service
class ProductService(private val repository: ProductRepository, private val apiConnector: EanApiConnector) {

    fun findAll(): List<ProductDto> = repository.findAll().map { ProductDto.fromDomain(it) }

    fun findOrNull(ean: EAN): ProductDto? {
        return repository.findByEan(ean.value)?.let { ProductDto.fromDomain(it) } ?: findAndCacheApiProduct(ean)
    }

    fun create(product: ProductDto, source: Source): ProductDto {
        return ProductDto.fromDomain(repository.save(product.toDomain(source)))
    }

    private fun findAndCacheApiProduct(ean: EAN): ProductDto? {
        return runBlocking { apiConnector.findByEan(ean) }?.let { create(it, Source.API) }
    }
}