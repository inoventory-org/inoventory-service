package com.inovex.inoventory.product

import com.inovex.inoventory.product.domain.Product
import org.springframework.data.jpa.repository.JpaRepository

interface ProductRepository : JpaRepository<Product, Long>