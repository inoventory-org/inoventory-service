package com.inovex.inoventory.ean.open_food_facts.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductResponse(
    val product: Product? = null,
    val code: String? = null,
    val status: Int = 0, // Boolean

    @SerialName("status_verbose")
    val statusVerbose: String? = null,
)