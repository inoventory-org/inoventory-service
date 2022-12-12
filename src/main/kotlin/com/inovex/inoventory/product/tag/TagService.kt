package com.inovex.inoventory.product.tag

import com.inovex.inoventory.product.tag.domain.Tag
import org.springframework.stereotype.Service

@Service
class TagService(private val repository: TagRepository) {
    fun getAll() = repository.findAll()

    fun create(tag: Tag): Tag {
        return repository.save(tag);
    }
}