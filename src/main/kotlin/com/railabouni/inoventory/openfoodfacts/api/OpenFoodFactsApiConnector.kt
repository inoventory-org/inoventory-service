package com.railabouni.inoventory.openfoodfacts.api

import com.railabouni.inoventory.openfoodfacts.ProductsConnector
import com.railabouni.inoventory.openfoodfacts.api.dto.ProductResponse
import com.railabouni.inoventory.openfoodfacts.api.dto.SearchResponse
import com.railabouni.inoventory.product.dto.EAN
import com.railabouni.inoventory.product.dto.Product
import com.railabouni.inoventory.product.entity.ProductEntity
import com.railabouni.inoventory.product.search.SearchCriteria
import com.railabouni.inoventory.product.search.SearchOperator
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.FormPart
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

//Example for usage when multiple APIs can be requested:
//@ConditionalOnProperty(value = ["app.ean-connector.type"], havingValue = "food")
@Component
class OpenFoodFactsApiConnector(
    val httpClient: HttpClient,
    @Value("\${openfoodfacts.user-id:inoventory}") private val offUserId: String,
    @Value("\${openfoodfacts.password:}") private val offPassword: String,
) : EanApiConnector, ProductsConnector {

    override suspend fun findByEan(ean: EAN): Product? {
        val url = "https://world.openfoodfacts.net/api/v3/product/${ean.value}.json&fields=$fields"
        val result = httpClient.get(url)
        if (result.status != HttpStatusCode.OK)
            return null

        val response = result.body<ProductResponse>()
        require(response.product != null)

        val product = response.product
        require(product.productName != null)

        return product.toProductDto()
    }

    override suspend fun search(criteria: List<SearchCriteria>): List<Product> {
        val searchTerm = criteria.singleOrNull {
            it.field == ProductEntity::name.name && it.operator == SearchOperator.Like
        }?.value ?: return listOf()

        val url = "https://world.openfoodfacts.net/cgi/search.pl?search_terms=$searchTerm&json=1&fields=$fields&page_size=$pageSize&page=1"
        val result = httpClient.get(url)
        if (result.status != HttpStatusCode.OK)
            return listOf()

        val response = result.body<SearchResponse>()
        return response.products.map { it.toProductDto() }
    }

    /**
     * Submits (creates or updates) a product in the OpenFoodFacts database.
     *
     * Uses the legacy product_jqm2.pl API to submit product metadata, then uploads
     * each image separately via the product_image_upload.pl endpoint.
     *
     * The [region] parameter controls the OFF subdomain (e.g. "world", "de", "us").
     * The [userId] is included in the `comment` field as required by the OFF API docs —
     * it is the UUID of the Inoventory user, not an OFF user ID.
     *
     * See: https://openfoodfacts.github.io/openfoodfacts-server/api/tutorials/adding-missing-products/
     */
    override suspend fun upsertToOpenFoodFacts(
        product: Product,
        images: Map<String, ByteArray>,
        userId: String,
        region: String
    ) {
        val baseUrl = "https://$region.openfoodfacts.net"

        // Step 1: Submit product text data via form-encoded POST
        val comment = "Edit by inoventory/0.0.1 - $userId"
        httpClient.submitForm(
            url = "$baseUrl/cgi/product_jqm2.pl",
            formParameters = Parameters.build {
                append("code", product.ean.value)
                append("user_id", offUserId)
                append("password", offPassword)
                append("comment", comment)
                product.name.takeIf { it.isNotBlank() }?.let { append("product_name", it) }
                product.brands?.takeIf { it.isNotBlank() }?.let { append("add_brands", it) }
                product.weight?.takeIf { it.isNotBlank() }?.let { append("quantity", it) }
            }
        ).also { response ->
            if (response.status != HttpStatusCode.OK) {
                throw Exception("Failed to submit product to OFF: ${response.status}")
            }
        }

        // Step 2: Upload each image separately
        for ((imageType, imageBytes) in images) {
            uploadImage(baseUrl, product.ean.value, imageType, imageBytes)
        }
    }

    private suspend fun uploadImage(
        baseUrl: String,
        barcode: String,
        imageType: String,
        imageBytes: ByteArray
    ) {
        httpClient.submitFormWithBinaryData(
            url = "$baseUrl/cgi/product_image_upload.pl",
            formData = formData {
                append("code", barcode)
                append("user_id", offUserId)
                append("password", offPassword)
                append("imagefield", imageType)
                append(
                    key = "imgupload_$imageType",
                    value = imageBytes,
                    headers = Headers.build {
                        append(HttpHeaders.ContentType, "image/jpeg")
                        append(HttpHeaders.ContentDisposition, "filename=\"${imageType}.jpg\"")
                    }
                )
            }
        ).also { response ->
            if (response.status != HttpStatusCode.OK) {
                throw Exception("Failed to upload $imageType image to OFF: ${response.status}")
            }
        }
    }

    companion object {
        private const val fields = "code,product_name,image_url,image_thumb_url,brands,categories_hierarchy"
        private const val pageSize = 10
    }
}