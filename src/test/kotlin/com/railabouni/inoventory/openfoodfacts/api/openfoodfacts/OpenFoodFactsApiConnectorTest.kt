package com.railabouni.inoventory.openfoodfacts.api

import com.railabouni.inoventory.openfoodfacts.api.dto.ProductResponse
import com.railabouni.inoventory.product.dto.EAN
import com.railabouni.inoventory.product.dto.Product
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
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import com.railabouni.inoventory.openfoodfacts.api.dto.Product as OpenFoodFactsProduct

private const val DEFAULT_USER_AGENT = "inoventory/0.0.1 (eilabouni.rudy@gmail.com)"
private const val OFF_USER_ID = "test-user-id"
private const val OFF_PASSWORD = "test-password"

class OpenFoodFactsApiConnectorTest {

    private val apiConnector = OpenFoodFactsApiConnector(
        httpClient,
        OFF_USER_ID,
        OFF_PASSWORD,
        "https://{region}.openfoodfacts.net"
    )

    @BeforeEach
    fun clearCapturedRequests() {
        capturedRequests.clear()
    }

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

    @Test
    fun `add() submits product fields to OFF with correct form parameters`() {
        val barcode = "12345678"
        val product = Product(
            name = "Test Product",
            ean = EAN(barcode),
            brands = "Test Brand"
        )

        runBlocking {
            // Should not throw
            apiConnector.upsertToOpenFoodFacts(
                product = product,
                images = emptyMap(),
                userId = "user-abc-123",
                language = "en",
                region = "world"
            )
        }

        // The last captured request body should contain the barcode and product name
        val productRequest = capturedRequests.lastOrNull { req ->
            req.url.toString().contains("product_jqm2.pl")
        }
        assertNotNull(productRequest, "Expected a request to product_jqm2.pl")
        runBlocking {
            val bodyText = productRequest!!.body.toByteArray().decodeToString()
            assertTrue(bodyText.contains("code=$barcode"), "Expected code in body, got: $bodyText")
            assertTrue(bodyText.contains("product_name=Test+Product") || bodyText.contains("product_name=Test%20Product"),
                "Expected product_name in body, got: $bodyText")
            assertTrue(bodyText.contains("user_id=$OFF_USER_ID"), "Expected user_id in body, got: $bodyText")
            assertTrue(bodyText.contains("comment="), "Expected comment in body, got: $bodyText")
            assertTrue(bodyText.contains("user-abc-123"), "Expected userId in comment, got: $bodyText")
            assertTrue(bodyText.contains("lc=en"), "Expected lc in body, got: $bodyText")
        }
    }

    @Test
    fun `add() uses specified region in URL`() {
        val product = Product(name = "Test Product", ean = EAN("12345678"))

        runBlocking {
            apiConnector.upsertToOpenFoodFacts(
                product = product,
                images = emptyMap(),
                userId = "user-abc",
                language = "de",
                region = "de"
            )
        }

        val productRequest = capturedRequests.lastOrNull { req ->
            req.url.toString().contains("product_jqm2.pl")
        }
        assertNotNull(productRequest)
        assertTrue(
            productRequest!!.url.host.contains("de."),
            "Expected 'de.' region in URL, got: ${productRequest.url.host}"
        )
        runBlocking {
            val bodyText = productRequest.body.toByteArray().decodeToString()
            assertTrue(bodyText.contains("cc=de"), "Expected cc in body, got: $bodyText")
            assertTrue(bodyText.contains("lc=de"), "Expected lc in body, got: $bodyText")
        }
    }

    @Test
    fun `add() uploads images with language specific field names`() {
        val product = Product(name = "", ean = EAN("12345678"))

        runBlocking {
            apiConnector.upsertToOpenFoodFacts(
                product = product,
                images = mapOf("ingredients" to "img".toByteArray()),
                userId = "user-abc",
                language = "fr",
                region = "world"
            )
        }

        val imageRequest = capturedRequests.lastOrNull { req ->
            req.url.toString().contains("product_image_upload.pl")
        }
        assertNotNull(imageRequest)
        runBlocking {
            val bodyText = imageRequest!!.body.toByteArray().decodeToString()
            assertTrue(bodyText.contains("ingredients_fr"), "Expected language-specific image marker in body, got: $bodyText")
        }
    }

    @Test
    fun `add() throws when OFF returns status not ok`() {
        val failingHttpClient = HttpClient(
            engine = MockEngine { request ->
                respond(
                    status = HttpStatusCode.OK,
                    content = """{"status":"status not ok","status_verbose":"bad request","debug":"missing data"}""",
                    headers = headersOf("content-type", "application/json")
                )
            }
        ) {
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
        val failingConnector = OpenFoodFactsApiConnector(
            failingHttpClient,
            OFF_USER_ID,
            OFF_PASSWORD,
            "https://{region}.openfoodfacts.net"
        )

        val error = assertThrows<Exception> {
            runBlocking {
                failingConnector.upsertToOpenFoodFacts(
                    product = Product(name = "", ean = EAN("12345678")),
                    images = emptyMap(),
                    userId = "user-abc",
                    language = "en",
                    region = "world"
                )
            }
        }

        assertTrue(error.message!!.contains("bad request"))
    }

    companion object {
        private lateinit var httpClient: HttpClient
        private val capturedRequests = mutableListOf<HttpRequestData>()

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
            val mockEngine = MockEngine { request ->
                capturedRequests.add(request)
                when {
                    request.url.toString().encodeURLPath().contains("12345678.json") ->
                        respond(
                            status = HttpStatusCode.OK,
                            content = Json.encodeToString(mockResponse),
                            headers = headersOf("content-type", "application/json")
                        )
                    request.url.toString().contains("product_jqm2.pl") ||
                    request.url.toString().contains("product_image_upload.pl") ->
                        respond(
                            status = HttpStatusCode.OK,
                            content = """{"status": 1, "status_verbose": "fields saved"}""",
                            headers = headersOf("content-type", "application/json")
                        )
                    else ->
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
