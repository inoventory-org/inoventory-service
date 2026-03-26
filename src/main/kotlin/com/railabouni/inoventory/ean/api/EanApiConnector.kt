package com.railabouni.inoventory.ean.api

import com.railabouni.inoventory.product.dto.EAN
import com.railabouni.inoventory.product.dto.Product
import com.railabouni.inoventory.product.search.SearchCriteria

interface EanApiConnector {
    suspend fun findByEan(ean: EAN) : Product?
    suspend fun search(criteria: List<SearchCriteria>) : List<Product>
}