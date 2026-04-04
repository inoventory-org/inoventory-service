package com.railabouni.inoventory.product

import com.railabouni.inoventory.openfoodfacts.ProductsConnector
import com.railabouni.inoventory.openfoodfacts.api.EanApiConnector
import com.railabouni.inoventory.product.dto.EAN
import com.railabouni.inoventory.product.dto.Product
import com.railabouni.inoventory.product.search.SearchCriteria
import com.railabouni.inoventory.user.service.CurrentUserService
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class ProductService(
    private val apiConnector: EanApiConnector,
    private val productsConnector: ProductsConnector,
    private val cache: ProductMemoryCache,
    private val currentUserService: CurrentUserService,
) {

    fun findAll(searchCriteria: List<SearchCriteria> = listOf()): List<Product> {
        if (searchCriteria.isEmpty()) {
            return listOf()
        }
        val results = runBlocking { apiConnector.search(searchCriteria) }
        cache.putAll(results)
        return results
    }

    fun scan(ean: EAN, fresh: Boolean = false): Product? {
        if (!fresh) {
            cache.get(ean.value)?.let { return it }
        }
        val product = runBlocking { apiConnector.findByEan(ean) }
        product?.let { cache.put(it) }
        return product
    }

    fun cacheProduct(product: Product): Product {
        cache.put(product)
        return product
    }

    /**
     * Submits a product (new or existing) to the OpenFoodFacts database.
     *
     * Works as an upsert: the OFF API identifies the product by its barcode and will
     * create it if it doesn't exist, or update its fields if it does.
     *
     * Images are keyed by their OFF field name: "front", "ingredients", "nutrition".
     *
     * @param product the product data to submit
     * @param frontImage optional front-of-pack image
     * @param ingredientsImage optional ingredients list image
     * @param nutritionImage optional nutritional info image
     * @param region OFF region subdomain (e.g. "world", "de", "us"); defaults to "world"
     */
    fun submitNewProduct(
        product: Product,
        frontImage: MultipartFile?,
        ingredientsImage: MultipartFile?,
        nutritionImage: MultipartFile?,
        language: String = "en",
        region: String = "world"
    ) {
        val currentUser = currentUserService.getCurrentUser()

        val images = buildMap<String, ByteArray> {
            frontImage?.takeIf { !it.isEmpty }?.let { put("front", it.bytes) }
            ingredientsImage?.takeIf { !it.isEmpty }?.let { put("ingredients", it.bytes) }
            nutritionImage?.takeIf { !it.isEmpty }?.let { put("nutrition", it.bytes) }
        }

        runBlocking {
            productsConnector.upsertToOpenFoodFacts(
                product = product,
                images = images,
                userId = currentUser.id.toString(),
                language = language,
                region = region
            )
        }
    }
}
