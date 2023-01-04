package com.inovex.inoventory.product.tag.dto

import com.inovex.inoventory.product.tag.domain.Tag

data class TagDto(val name: String) {
    fun toEntity() = Tag(
        name = name
    )

    companion object {
        fun fromEntity(tag: Tag) = TagDto(
            name = tag.name
        )
    }
}