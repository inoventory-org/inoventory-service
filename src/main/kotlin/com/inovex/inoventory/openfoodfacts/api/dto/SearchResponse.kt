package com.inovex.inoventory.openfoodfacts.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class SearchResponse (
    val count: Int,
    val products: List<Product>
)