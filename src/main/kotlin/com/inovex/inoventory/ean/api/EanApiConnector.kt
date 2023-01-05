package com.inovex.inoventory.ean.api

import com.inovex.inoventory.product.dto.EAN
import com.inovex.inoventory.product.dto.Product
import com.inovex.inoventory.product.search.SearchCriteria

interface EanApiConnector {
    suspend fun findByEan(ean: EAN) : Product?
    suspend fun search(criteria: List<SearchCriteria>) : List<Product>
}