package com.inovex.inoventory.product

import com.inovex.inoventory.product.dto.EAN
import com.inovex.inoventory.product.dto.Product
import com.inovex.inoventory.product.entity.SourceEntity
import com.inovex.inoventory.product.search.SearchString
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/products")
class ProductController(private val service: ProductService) {
    @GetMapping
    fun getAll(@RequestParam(required = false) search: SearchString?): List<Product> {
        val searchCriteria = search?.extractSearchCriteria()
        return service.findAll(searchCriteria ?: listOf())
    }

    @GetMapping(params = ["ean"])
    fun scan(@RequestParam ean: String) = service.scan(EAN(ean))

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun post(@RequestBody product: Product) = service.upsert(product, SourceEntity.USER)
}