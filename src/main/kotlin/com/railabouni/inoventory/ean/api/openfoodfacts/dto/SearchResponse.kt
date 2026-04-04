<<<<<<<< HEAD:src/main/kotlin/com/railabouni/inoventory/openfoodfacts/api/dto/SearchResponse.kt
package com.railabouni.inoventory.openfoodfacts.api.dto
========
package com.railabouni.inoventory.ean.api.openfoodfacts.dto
>>>>>>>> main:src/main/kotlin/com/railabouni/inoventory/ean/api/openfoodfacts/dto/SearchResponse.kt

import kotlinx.serialization.Serializable

@Serializable
data class SearchResponse (
    val count: Int,
    val products: List<Product>
)