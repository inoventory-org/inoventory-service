package com.inovex.inoventory.product

import com.inovex.inoventory.product.domain.Product
import com.inovex.inoventory.product.domain.Source
import io.mockk.every
import io.mockk.mockk

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ProductServiceTest {

    private val productRepository: ProductRepository = mockk()
    private val productService = ProductService(productRepository)

    @Test
    fun testGetAllEmpty() {
        // no products were added
        every { productRepository.findAll() } returns emptyList()

        // when
        val products = productService.getAll()

        // then
        assertTrue(products.isEmpty())
    }

    @Test
    fun testAddProduct() {
        // given
        val product = Product(1, "Test Product", "978–0521425575", Source.API, setOf())
        every { productRepository.save(any()) } returnsArgument 0

        // when
        val newProduct = productService.create(product)

        // then
        assertEquals(newProduct.id, product.id)
        assertEquals(newProduct.name, product.name)
        assertEquals(newProduct.EAN, product.EAN)
        assertEquals(newProduct.source, product.source)
        assertEquals(newProduct.tags, product.tags)
    }
}