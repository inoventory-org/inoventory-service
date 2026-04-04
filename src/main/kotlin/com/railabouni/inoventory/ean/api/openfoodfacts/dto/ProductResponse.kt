<<<<<<<< HEAD:src/main/kotlin/com/railabouni/inoventory/openfoodfacts/api/dto/ProductResponse.kt
package com.railabouni.inoventory.openfoodfacts.api.dto
========
package com.railabouni.inoventory.ean.api.openfoodfacts.dto
>>>>>>>> main:src/main/kotlin/com/railabouni/inoventory/ean/api/openfoodfacts/dto/ProductResponse.kt

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