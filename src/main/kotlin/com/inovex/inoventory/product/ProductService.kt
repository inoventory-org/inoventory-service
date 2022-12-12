package com.inovex.inoventory.product

import com.inovex.inoventory.product.domain.Product
import org.springframework.stereotype.Service

@Service
class ProductService(private val repository: ProductRepository) {
    fun getAll() = repository.findAll()

    fun create(product: Product): Product {
        return repository.save(product);
    }
}