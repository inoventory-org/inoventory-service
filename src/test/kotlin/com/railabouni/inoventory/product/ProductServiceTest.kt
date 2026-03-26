package com.railabouni.inoventory.product

import com.railabouni.inoventory.ean.api.EanApiConnector
import com.railabouni.inoventory.product.dto.EAN
import com.railabouni.inoventory.product.dto.Product
import io.mockk.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.Duration

class ProductServiceTest {

    private val apiConnector: EanApiConnector = mockk()
    private val cache = ProductMemoryCache(ProductCacheProperties(Duration.ofMinutes(5), 10))
    private val productService = ProductService(apiConnector, cache)

    @Test
    fun `findAll() works with empty repository`() {
        // given
        // when
        val products = productService.findAll()

        // then
        assertTrue(products.isEmpty())
    }

    @Test
    fun `cacheProduct() works`() {
        // given
        val product = Product(name = "Test Product", ean = EAN("12345678"))

        // when
        val newProduct = productService.cacheProduct(product)

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
        productService.cacheProduct(cachedProduct)

        // when
        val actual = productService.scan(ean)

        // then
        assertEquals(cachedProduct, actual)
    }


    @Test
    fun `findOrNull() caches product for new EAN`() {
        // given
        val ean = EAN("12345678")
        val newProduct = createMockProduct(ean)
        coEvery { apiConnector.findByEan(ean) } returns newProduct

        // when
        val actual = productService.scan(ean)

        // then
        assertEquals(newProduct, actual)
        coVerify(exactly = 1) { apiConnector.findByEan(ean) }
    }

    @Test
    fun `scan() with fresh=true forces new product fetch`() {
        // given
        val ean = EAN("12345678")
        val newProduct = createMockProduct(ean)
        coEvery { apiConnector.findByEan(ean) } returns newProduct

        // when
        productService.scan(ean) // first call should cache product

        productService.scan(ean) // second call should get cached product
        val actual = productService.scan(ean, true) // by setting fresh=true, product should be fetched and cached again


        // then
        coVerify(exactly = 2) { apiConnector.findByEan(ean)}
        assertEquals(newProduct, actual)
    }


    private fun createMockProduct(ean: EAN) = Product(
        ean = ean,
        name = "I'm cached!"
    )
}
