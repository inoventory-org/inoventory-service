package com.inovex.inoventory.product.dto

import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class EANTest {
    @Test
    fun `init() works for valid EAN13`() {
        assertDoesNotThrow { EAN("0123456789123") }
    }

    @Test
    fun `init() works for valid EAN8`() {
        assertDoesNotThrow { EAN("12345678") }
    }

    @Test
    fun `init() throws exception for invalid EAN8`() {
        assertThrows<IllegalArgumentException> { EAN("0000") }
    }
}