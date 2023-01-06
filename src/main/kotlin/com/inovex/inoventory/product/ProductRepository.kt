package com.inovex.inoventory.product

import com.inovex.inoventory.product.entity.ProductEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import java.time.Instant

interface ProductRepository : JpaRepository<ProductEntity, String>, JpaSpecificationExecutor<ProductEntity> {
    fun findByEan(ean: String) : ProductEntity?

    fun findByCachedTimestampBefore(instant: Instant) : List<ProductEntity>
}