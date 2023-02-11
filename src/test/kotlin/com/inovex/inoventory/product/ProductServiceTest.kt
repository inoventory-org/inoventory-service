package com.inovex.inoventory.product

import com.inovex.inoventory.ean.api.EanApiConnector
import com.inovex.inoventory.product.dto.EAN
import com.inovex.inoventory.product.dto.Product
import com.inovex.inoventory.product.entity.ProductEntity
import com.inovex.inoventory.product.entity.SourceEntity
import com.inovex.inoventory.product.tag.entity.TagEntity
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.data.jpa.domain.Specification

class ProductServiceTest {

    private val productRepository: ProductRepository = mockk()
    private val apiConnector: EanApiConnector = mockk()
    private val productService = ProductService(productRepository, apiConnector)

    @Test
    fun `findAll() works with empty repository`() {
        // given
        // no products were added
        every { productRepository.findAll(any<Specification<ProductEntity>>()) } returns emptyList()
        coEvery { apiConnector.search(any()) } returns emptyList()

        // when
        val products = productService.findAll()

        // then
        assertTrue(products.isEmpty())
    }

    @Test
    fun `upsert() works`() {
        // given
        val product = Product(name = "Test Product", ean = EAN("12345678"))
        every { productRepository.save(any()) } returnsArgument 0

        // when
        val newProduct = productService.upsert(product, SourceEntity.USER)

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
        val actual = productService.scan(ean)

        // then
        assertEquals(Product.fromEntity(cachedProduct), actual)
    }

    @Test
    fun `findOrNull() caches product for new EAN`() {
        // given
        val ean = EAN("12345678")
        val newProduct = createMockProduct(ean)
        val newProductDto = Product.fromEntity(newProduct)

        every { productRepository.findByEan(ean.value) } returns null
        every { productRepository.save(match { it.ean == newProduct.ean }) } returns newProduct
        coEvery { apiConnector.findByEan(ean) } returns newProductDto

        // when
        val actual = productService.scan(ean)

        // then
        verify(exactly = 1) { productRepository.save(match { it.ean == newProduct.ean }) }
        assertEquals(newProductDto, actual)
    }

    private fun createMockProduct(ean: EAN) = ProductEntity(
        ean = ean.value,
        name = "I'm cached!",
        source = SourceEntity.API,
        tags = listOf(TagEntity(1, "en:breakfast"))
    )
}