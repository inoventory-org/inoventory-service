package com.inovex.inoventory.product

import com.inovex.inoventory.ean.EanApiConnector
import com.inovex.inoventory.product.domain.Product
import com.inovex.inoventory.product.domain.Source
import com.inovex.inoventory.product.dto.EAN
import com.inovex.inoventory.product.dto.ProductDto
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ProductServiceTest {

    private val productRepository: ProductRepository = mockk()
    private val apiConnector: EanApiConnector = mockk()
    private val productService = ProductService(productRepository, apiConnector)

    @Test
    fun `findAll() works with empty repository`() {
        // given
        // no products were added
        every { productRepository.findAll() } returns emptyList()

        // when
        val products = productService.findAll()

        // then
        assertTrue(products.isEmpty())
    }

    @Test
    fun `create() works`() {
        // given
        val product = ProductDto(name = "Test Product", ean = EAN("12345678"))
        every { productRepository.save(any()) } returnsArgument 0

        // when
        val newProduct = productService.create(product, Source.USER)

        // then
        assertEquals(newProduct.name, product.name)
        assertEquals(newProduct.ean, product.ean)
        assertEquals(newProduct.tags, product.tags)
    }

    @Test
    fun `findOrNull() returns cached product`() {
        // given
        val ean = EAN("12345678")
        val cachedProduct = createMockProduct(ean)

        every { productRepository.findByEan(ean.value) } returns cachedProduct

        // when
        val actual = productService.findOrNull(ean)

        // then
        assertEquals(ProductDto.fromDomain(cachedProduct), actual)
    }

    @Test
    fun `findOrNull() caches product for new EAN`() {
        // given
        val ean = EAN("12345678")
        val newProduct = createMockProduct(ean)
        val newProductDto = ProductDto.fromDomain(newProduct)

        every { productRepository.findByEan(ean.value) } returns null
        every { productRepository.save(newProduct) } returns newProduct
        coEvery { apiConnector.findByEan(ean) } returns newProductDto

        // when
        val actual = productService.findOrNull(ean)

        // then
        verify (exactly = 1) { productRepository.save(newProduct) }
        assertEquals(newProductDto, actual)
    }

    private fun createMockProduct(ean: EAN) = Product(
        id = 42,
        name = "I'm cached!",
        ean = ean.value,
        source = Source.API,
        tags = setOf()
    )
}