package com.inovex.inoventory.ean.api.openfoodfacts

import com.inovex.inoventory.ean.api.openfoodfacts.dto.ProductResponse
import com.inovex.inoventory.product.dto.EAN
import com.inovex.inoventory.product.dto.Product
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import com.inovex.inoventory.ean.api.openfoodfacts.dto.Product as OpenFoodFactsProduct

private const val DEFAULT_USER_AGENT = "inoventory/0.0.1 (eilabouni.rudy@gmail.com)"

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
        val expected = Product(
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

    @Test
    fun `httpClient sets user agent`() {
        // given
        runBlocking {
            val result = httpClient.get("www.someurl.com/12345678.json")
            assertTrue(result.request.headers.contains(HttpHeaders.UserAgent, DEFAULT_USER_AGENT))
        }
    }


    companion object {
        private lateinit var httpClient: HttpClient

        val mockResponse = ProductResponse(
            product = OpenFoodFactsProduct(
                code = "12345678",
                productName = "mockedProduct",
                imageUrl = "image.png",
                imageThumbUrl = "thumb.png"
            )
        )

        @JvmStatic
        @BeforeAll
        fun setup() {
            val mockEngine = MockEngine {
                if (it.url.toString().encodeURLPath().contains("12345678.json")) {
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
                install(UserAgent) {
                    agent = DEFAULT_USER_AGENT
                }
            }
        }
    }
}