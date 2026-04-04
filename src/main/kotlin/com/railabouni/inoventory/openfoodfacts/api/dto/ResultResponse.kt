package com.railabouni.inoventory.openfoodfacts.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResultResponse(
    val id: String? = null,
    @SerialName("lc_name")
    val lcName: String? = null,
    val name: String? = null,
    val status: String? = null,
    @SerialName("status_verbose")
    val statusVerbose: String? = null,
    val debug: String? = null,
    val code: String? = null,
    val imagefield: String? = null,
)
