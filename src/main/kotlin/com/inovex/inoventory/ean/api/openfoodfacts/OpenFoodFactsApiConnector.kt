package com.inovex.inoventory.ean.api.openfoodfacts

import com.inovex.inoventory.ean.api.EanApiConnector
import com.inovex.inoventory.ean.api.openfoodfacts.dto.ProductResponse
import com.inovex.inoventory.ean.api.openfoodfacts.dto.SearchResponse
import com.inovex.inoventory.product.dto.EAN
import com.inovex.inoventory.product.dto.Product
import com.inovex.inoventory.product.entity.ProductEntity
import com.inovex.inoventory.product.search.SearchCriteria
import com.inovex.inoventory.product.search.SearchOperator
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

//Example for usage when multiple APIs can be requested:
//@ConditionalOnProperty(value = ["app.ean-connector.type"], havingValue = "food")
@Component
class OpenFoodFactsApiConnector(val httpClient: HttpClient) : EanApiConnector {
    override suspend fun findByEan(ean: EAN): Product? {
        val url = "$baseUrl/api/v3/product/${ean.value}.json&fields=$fields"
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

        val url = "$baseUrl/cgi/search.pl?search_terms=$searchTerm&json=1&fields=$fields&page_size=$pageSize&page=1"
        val result = httpClient.get(url)
        println(result)
        if (result.status != HttpStatusCode.OK)
            return listOf()

        val response = result.body<SearchResponse>()
        return response.products.map { it.toProductDto() }
    }

    companion object {
        @Value("\${openfoodfacts.baseurl:https://de.openfoodfacts.net}")
        private const val baseUrl = "https://de.openfoodfacts.net"
        private const val fields = "code,product_name,image_url,image_thumb_url,brands,categories_hierarchy"
        private const val pageSize = 10
    }
}