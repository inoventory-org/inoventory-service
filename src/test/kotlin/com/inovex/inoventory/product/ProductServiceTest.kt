package com.inovex.inoventory.product

import com.inovex.inoventory.product.dto.EAN
import com.inovex.inoventory.product.dto.ProductDto
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ProductServiceTest {

    private val productRepository: ProductRepository = mockk()
    private val productService = ProductService(productRepository, mockk())

    @Test
    fun testGetAllEmpty() {
        // no products were added
        every { productRepository.findAll() } returns emptyList()

        // when
        val products = productService.findAll()

        // then
        assertTrue(products.isEmpty())
    }

    @Test
    fun testAddProduct() {
        // given
        val product = ProductDto(name = "Test Product", ean = EAN("12345678"))
        every { productRepository.save(any()) } returnsArgument 0

        // when
        val newProduct = productService.create(product)

        // then
//        assertEquals(newProduct.id, product.id)
        assertEquals(newProduct.name, product.name)
        assertEquals(newProduct.ean, product.ean)
        assertEquals(newProduct.tags, product.tags)
    }
}