package com.inovex.inoventory.ean.open_food_facts.dto

import kotlinx.serialization.json.JsonElement

data class Images(
    var other: MutableMap<String, JsonElement> = LinkedHashMap()
) {
    // Was: (key: String, value: Any)
    fun setDetail(key: String, value: JsonElement) {
        other[key] = value
    }
}