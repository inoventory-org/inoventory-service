package com.inovex.inoventory.ean.open_food_facts

import com.inovex.inoventory.ean.open_food_facts.dto.Product
import com.inovex.inoventory.ean.open_food_facts.dto.ProductResponse
import com.inovex.inoventory.product.dto.EAN
import com.inovex.inoventory.product.dto.ProductDto
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class OpenFoodFactsApiConnectorTest {

    private val apiConnector = OpenFoodFactsApiConnector(httpClient)

    @Test
    fun `findByEan() works when EAN is found`() {
        // given
        val ean = EAN("12345678")

        // when
        val result = runBlocking { apiConnector.findByEan(ean) }

        // then
        val mockProduct = mockResponse.product!!
        val expected = ProductDto(
            name = mockProduct.productName!!,
            ean = ean,
            imageUrl = mockProduct.imageUrl,
            thumbUrl = mockProduct.imageThumbUrl
        )
        assertEquals(expected, result)
    }

    @Test
    fun `findByEan() returns null when EAN is not found`() {
        // given
        val ean = EAN("00040400")

        // when
        val result = runBlocking { apiConnector.findByEan(ean) }

        // then
        assertNull(result)
    }

    companion object {
        private lateinit var httpClient: HttpClient

        val mockResponse = ProductResponse(
            product = Product(
                productName = "mockedProduct",
                imageUrl = "image.png",
                imageThumbUrl = "thumb.png"
            )
        )
        @JvmStatic
        @BeforeAll
        fun setup() {


            val mockEngine = MockEngine {
                if (it.url.toString().encodeURLPath().endsWith("12345678.json")) {
                    respond(
                        status = HttpStatusCode.OK,
                        content = Json.encodeToString(mockResponse),
                        headers = headersOf("content-type", "application/json")
                    )
                } else {
                    respond(
                        status = HttpStatusCode.NotFound,
                        content = "",
                    )
                }
            }

            httpClient = HttpClient(engine = mockEngine) {
                expectSuccess = false
                install(ContentNegotiation) {
                    json(
                        Json {
                            prettyPrint = true
                            encodeDefaults = true
                            ignoreUnknownKeys = true
                        }
                    )
                }
            }
        }
    }
}