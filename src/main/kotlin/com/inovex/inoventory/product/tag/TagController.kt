package com.inovex.inoventory.product.tag

import com.inovex.inoventory.product.tag.domain.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/tags")
class TagController(private val service: TagService) {
    @GetMapping
    fun getAll() = service.getAll()

    @PostMapping
    fun post(@RequestBody tag: Tag) = service.create(tag)
}