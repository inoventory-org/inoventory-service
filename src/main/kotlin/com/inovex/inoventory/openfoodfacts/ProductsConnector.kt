package com.inovex.inoventory.openfoodfacts

import com.inovex.inoventory.product.dto.Product

interface ProductsConnector {
    suspend fun add(product: Product): Product
}