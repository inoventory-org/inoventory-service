package com.railabouni.inoventory.product

import com.railabouni.inoventory.openfoodfacts.ProductsConnector
import com.railabouni.inoventory.openfoodfacts.api.EanApiConnector
import com.railabouni.inoventory.product.dto.EAN
import com.railabouni.inoventory.product.dto.Product
import com.railabouni.inoventory.product.search.SearchCriteria
import com.railabouni.inoventory.product.search.SearchOperator
import com.railabouni.inoventory.user.dto.UserDto
import com.railabouni.inoventory.user.service.CurrentUserService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockMultipartFile
import java.time.Duration
import java.util.UUID

class ProductServiceTest {

    private val apiConnector: EanApiConnector = mockk()
    private val productsConnector: ProductsConnector = mockk(relaxed = true)
    private val currentUserService: CurrentUserService = mockk()
    private val cache = ProductMemoryCache(ProductCacheProperties(Duration.ofMinutes(5), 10))
    private val productService = ProductService(apiConnector, productsConnector, cache, currentUserService)

    @Test
    fun `findAll returns empty list when no search criteria are provided`() {
        val products = productService.findAll()

        assertTrue(products.isEmpty())
        coVerify(exactly = 0) { apiConnector.search(any()) }
    }

    @Test
    fun `findAll delegates search and caches results when criteria are provided`() {
        val criteria = listOf(SearchCriteria("name", "milk", SearchOperator.Like))
        val product = createMockProduct(EAN("12345678"))
        coEvery { apiConnector.search(criteria) } returns listOf(product)

        val products = productService.findAll(criteria)

        assertEquals(listOf(product), products)
        assertEquals(product, productService.scan(product.ean))
        coVerify(exactly = 1) { apiConnector.search(criteria) }
        coVerify(exactly = 0) { apiConnector.findByEan(product.ean) }
    }

    @Test
    fun `cacheProduct works`() {
        val product = createMockProduct(EAN("12345678"))

        val newProduct = productService.cacheProduct(product)

        assertEquals(newProduct.name, product.name)
        assertEquals(newProduct.ean, product.ean)
        assertEquals(newProduct.tags, product.tags)
    }

    @Test
    fun `scan returns cached product`() {
        val ean = EAN("12345678")
        val cachedProduct = createMockProduct(ean)
        productService.cacheProduct(cachedProduct)

        val actual = productService.scan(ean)

        assertEquals(cachedProduct, actual)
        coVerify(exactly = 0) { apiConnector.findByEan(ean) }
    }

    @Test
    fun `scan caches product for new EAN`() {
        val ean = EAN("12345678")
        val newProduct = createMockProduct(ean)
        coEvery { apiConnector.findByEan(ean) } returns newProduct

        val actual = productService.scan(ean)

        assertEquals(newProduct, actual)
        coVerify(exactly = 1) { apiConnector.findByEan(ean) }
    }

    @Test
    fun `scan with fresh true forces new product fetch`() {
        val ean = EAN("12345678")
        val newProduct = createMockProduct(ean)
        coEvery { apiConnector.findByEan(ean) } returns newProduct

        productService.scan(ean)
        productService.scan(ean)
        val actual = productService.scan(ean, true)

        coVerify(exactly = 2) { apiConnector.findByEan(ean) }
        assertEquals(newProduct, actual)
    }

    @Test
    fun `submitNewProduct forwards product images and current user to OFF connector`() {
        val userId = UUID.randomUUID()
        val product = createMockProduct(EAN("12345678"))
        val frontImage = MockMultipartFile("frontImage", "front.jpg", "image/jpeg", "front".toByteArray())
        val nutritionImage = MockMultipartFile("nutritionImage", "nutrition.jpg", "image/jpeg", "nutrition".toByteArray())
        every { currentUserService.getCurrentUser() } returns UserDto(userId, "tester")

        productService.submitNewProduct(
            product = product,
            frontImage = frontImage,
            ingredientsImage = null,
            nutritionImage = nutritionImage,
            region = "de"
        )

        coVerify(exactly = 1) {
            productsConnector.upsertToOpenFoodFacts(
                product = product,
                images = match {
                    it["front"]?.contentEquals("front".toByteArray()) == true &&
                        it["nutrition"]?.contentEquals("nutrition".toByteArray()) == true &&
                        "ingredients" !in it
                },
                userId = userId.toString(),
                region = "de"
            )
        }
    }

    private fun createMockProduct(ean: EAN) = Product(
        ean = ean,
        name = "I'm cached!"
    )
}
