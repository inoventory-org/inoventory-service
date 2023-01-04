package com.inovex.inoventory.product

import com.inovex.inoventory.product.entity.ProductEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface ProductRepository : JpaRepository<ProductEntity, Long>, JpaSpecificationExecutor<ProductEntity> {
    fun findByEan(ean: String) : ProductEntity?
}