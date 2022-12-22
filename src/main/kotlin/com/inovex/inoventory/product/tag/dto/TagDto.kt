package com.inovex.inoventory.product.tag.dto

import com.inovex.inoventory.product.tag.domain.Tag

data class TagDto(val name: String) {
    fun toDomain() = Tag(
        name = name
    )

    companion object {
        fun fromDomain(tag: Tag) = TagDto(
            name = tag.name
        )
    }
}