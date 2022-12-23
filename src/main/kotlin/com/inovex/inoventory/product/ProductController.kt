package com.inovex.inoventory.product

import com.inovex.inoventory.product.domain.Source
import com.inovex.inoventory.product.dto.EAN
import com.inovex.inoventory.product.dto.ProductDto
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/products")
class ProductController(private val service: ProductService) {
    @GetMapping
    fun getAll(): List<ProductDto> = service.findAll()

    @GetMapping(params = ["ean"])
    fun get(@RequestParam ean: String) = service.findOrNull(EAN(ean))

    @PostMapping
    fun post(@RequestBody product: ProductDto) = service.create(product, Source.USER)
}