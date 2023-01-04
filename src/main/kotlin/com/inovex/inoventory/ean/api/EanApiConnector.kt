package com.inovex.inoventory.ean.api

import com.inovex.inoventory.product.dto.EAN
import com.inovex.inoventory.product.dto.Product

interface EanApiConnector {
    suspend fun findByEan(ean: EAN) : Product?
}