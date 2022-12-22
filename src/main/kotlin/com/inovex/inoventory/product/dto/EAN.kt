package com.inovex.inoventory.product.dto

@JvmInline
value class EAN(val value: String) {
    init {
        require(isEAN8() || isEAN13())
    }

    private fun isEAN8() = "^\\d{8}\$".toRegex().matches(value)
    private fun isEAN13() = "^\\d{13}\$".toRegex().matches(value)

}
