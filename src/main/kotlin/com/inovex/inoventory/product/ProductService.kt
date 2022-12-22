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
         val result = runBlocking { apiConnector.findByEan(ean) }
        return result
    }

    fun create(product: ProductDto): ProductDto {
        return ProductDto.fromDomain(repository.save(product.toDomain(Source.USER)))
    }
}