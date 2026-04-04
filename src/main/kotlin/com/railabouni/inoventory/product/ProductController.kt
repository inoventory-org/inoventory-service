package com.railabouni.inoventory.product

import com.railabouni.inoventory.product.dto.EAN
import com.railabouni.inoventory.product.dto.Product
import com.railabouni.inoventory.product.search.SearchString
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/v1/products")
class ProductController(private val service: ProductService) {

    @GetMapping
    fun getAll(@RequestParam(required = false) search: SearchString?): List<Product> {
        val searchCriteria = search?.extractSearchCriteria()
        return service.findAll(searchCriteria ?: listOf())
    }

    @GetMapping(params = ["ean", "fresh"])
    fun scan(
        @RequestParam ean: String,
        @RequestParam(required = false, defaultValue = "false") fresh: Boolean
    ) = service.scan(EAN(ean), fresh)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun post(@RequestBody product: Product) = service.cacheProduct(product)

    /**
     * Creates or updates a product in the OpenFoodFacts database.
     *
     * This endpoint acts as an upsert: OFF identifies the product by its barcode (EAN).
     * If the product already exists in OFF, its fields will be updated; if not, it will be created.
     *
     * Accepts a multipart/form-data request with optional text fields and image parts.
     * The [region] parameter controls the OFF subdomain (e.g. "world", "de", "us").
     */
    @PutMapping("/{ean}", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun upsertToOpenFoodFacts(
        @PathVariable ean: String,
        @RequestParam(required = false) productName: String?,
        @RequestParam(required = false) brands: String?,
        @RequestParam(required = false) weight: String?,
        @RequestParam(required = false, defaultValue = "en") language: String,
        @RequestParam(required = false, defaultValue = "world") region: String,
        @RequestPart(required = false) frontImage: MultipartFile?,
        @RequestPart(required = false) ingredientsImage: MultipartFile?,
        @RequestPart(required = false) nutritionImage: MultipartFile?,
    ) {
        val product = Product(
            ean = EAN(ean),
            name = productName ?: "",
            brands = brands,
            weight = weight,
        )
        service.submitNewProduct(
            product = product,
            frontImage = frontImage,
            ingredientsImage = ingredientsImage,
            nutritionImage = nutritionImage,
            language = language,
            region = region
        )
    }
}
