package com.inovex.inoventory.product

import com.inovex.inoventory.product.entity.ProductEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ProductRepository : JpaRepository<ProductEntity, Long> {
    fun findByEan(ean: String) : ProductEntity?
}