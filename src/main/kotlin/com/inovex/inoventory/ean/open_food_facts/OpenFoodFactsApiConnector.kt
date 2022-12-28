package com.inovex.inoventory.ean.open_food_facts

import com.inovex.inoventory.ean.EanApiConnector
import com.inovex.inoventory.ean.open_food_facts.dto.ProductResponse
import com.inovex.inoventory.product.dto.EAN
import com.inovex.inoventory.product.dto.ProductDto
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.springframework.stereotype.Component

//Example for usage when multiple APIs can be requested:
//@ConditionalOnProperty(value = ["app.ean-connector.type"], havingValue = "food")
@Component
class OpenFoodFactsApiConnector(val httpClient: HttpClient) : EanApiConnector {
    override suspend fun findByEan(ean: EAN): ProductDto? {
        val result = httpClient.get("$baseUrl${ean.value}.json")
        if (result.status != HttpStatusCode.OK)
            return null

        val response = result.body<ProductResponse>()
        require(response.product != null)

        val product = response.product
        require(product.productName != null)

        return ProductDto(
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