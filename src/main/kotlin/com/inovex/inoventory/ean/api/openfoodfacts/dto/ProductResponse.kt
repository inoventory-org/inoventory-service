package com.inovex.inoventory.ean.api.openfoodfacts.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProductResponse(
    val product: Product? = null,
    val code: String? = null,
    val status: String = "",
    val errors: List<String> = emptyList(),
    val warnings: List<String> = emptyList(),
    val result: ResultResponse? = null,
)