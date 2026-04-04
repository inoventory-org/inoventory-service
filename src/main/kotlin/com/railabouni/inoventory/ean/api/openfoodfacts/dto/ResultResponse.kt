<<<<<<<< HEAD:src/main/kotlin/com/railabouni/inoventory/openfoodfacts/api/dto/ResultResponse.kt
package com.railabouni.inoventory.openfoodfacts.api.dto
========
package com.railabouni.inoventory.ean.api.openfoodfacts.dto
>>>>>>>> main:src/main/kotlin/com/railabouni/inoventory/ean/api/openfoodfacts/dto/ResultResponse.kt

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResultResponse(
    val id: String? = null,
    @SerialName("lc_name")
    val lcName: String? = null,
    val name: String? = null,
)