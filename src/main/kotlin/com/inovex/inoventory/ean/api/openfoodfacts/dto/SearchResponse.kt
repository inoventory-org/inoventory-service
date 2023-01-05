package com.inovex.inoventory.ean.api.openfoodfacts.dto

import kotlinx.serialization.Serializable

@Serializable
data class SearchResponse (
    val count: Int,
    val products: List<Product>
)