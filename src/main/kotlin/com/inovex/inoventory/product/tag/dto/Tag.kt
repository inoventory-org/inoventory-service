package com.inovex.inoventory.product.tag.dto

import com.inovex.inoventory.product.tag.entity.TagEntity

data class Tag(val name: String) {
    fun toEntity() = TagEntity(
        name = name
    )

    companion object {
        fun fromEntity(tag: TagEntity) = Tag(
            name = tag.name
        )
    }
}