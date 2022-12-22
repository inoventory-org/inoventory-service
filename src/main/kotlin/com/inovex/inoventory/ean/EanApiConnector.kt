package com.inovex.inoventory.ean

import com.inovex.inoventory.product.dto.EAN
import com.inovex.inoventory.product.dto.ProductDto

interface EanApiConnector {
    suspend fun findByEan(ean: EAN) : ProductDto?
}