package com.inovex.inoventory.product

import com.inovex.inoventory.product.domain.Product
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/products")
class ProductController(private val service: ProductService) {
    @GetMapping
    fun getAll() = service.getAll()

    @PostMapping
    fun post(@RequestBody product: Product) = service.create(product)
}