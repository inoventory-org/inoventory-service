package com.inovex.inoventory.product.tag

import com.inovex.inoventory.product.tag.entity.TagEntity
import org.springframework.stereotype.Service

@Service
class TagService(private val repository: TagRepository) {
    fun getAll() : List<TagEntity> = repository.findAll()

    fun create(tag: TagEntity): TagEntity {
        return repository.save(tag)
    }
}