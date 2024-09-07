package com.inovex.inoventory.openfoodfacts.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResultResponse(
    val id: String? = null,
    @SerialName("lc_name")
    val lcName: String? = null,
    val name: String? = null,
)