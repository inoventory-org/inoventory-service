package com.inovex.inoventory.product

import com.inovex.inoventory.product.entity.SourceEntity
import com.inovex.inoventory.product.dto.EAN
import com.inovex.inoventory.product.dto.Product
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/products")
class ProductController(private val service: ProductService) {
    @GetMapping
    fun getAll(): List<Product> = service.findAll()

    @GetMapping(params = ["ean"])
    fun get(@RequestParam ean: String) = service.findOrNull(EAN(ean))

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun post(@RequestBody product: Product) = service.create(product, SourceEntity.USER)
}