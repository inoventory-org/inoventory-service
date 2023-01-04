package com.inovex.inoventory.ean.api.openfoodfacts

import com.inovex.inoventory.ean.api.EanApiConnector
import com.inovex.inoventory.ean.api.openfoodfacts.dto.ProductResponse
import com.inovex.inoventory.product.dto.EAN
import com.inovex.inoventory.product.dto.Product
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.springframework.stereotype.Component

//Example for usage when multiple APIs can be requested:
//@ConditionalOnProperty(value = ["app.ean-connector.type"], havingValue = "food")
@Component
class OpenFoodFactsApiConnector(val httpClient: HttpClient) : EanApiConnector {
    override suspend fun findByEan(ean: EAN): Product? {
        val result = httpClient.get("$baseUrl${ean.value}.json")
        if (result.status != HttpStatusCode.OK)
            return null

        val response = result.body<ProductResponse>()
        require(response.product != null)

        val product = response.product
        require(product.productName != null)

        return Product(
            name = product.productName,
            ean = ean,
            brands = product.brands,
            imageUrl = product.imageUrl,
            thumbUrl = product.imageThumbUrl
        )
    }

    companion object {
        private const val baseUrl = "https://world.openfoodfacts.org/api/v0/product/"
    }
}