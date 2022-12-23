package com.inovex.inoventory.product.dto

import com.inovex.inoventory.product.domain.Product
import com.inovex.inoventory.product.domain.Source
import com.inovex.inoventory.product.tag.dto.TagDto

data class ProductDto(
    val id: Long? = null,
    val name: String,
    val ean: EAN? = null,
    val imageUrl: String?,
    val thumbUrl: String?,
    val tags: Set<TagDto> = setOf()
) {
    fun toDomain(source: Source) = Product(
        id = id,
        name = name,
        ean = ean?.value,
        source = source,
        imageUrl = imageUrl,
        thumbUrl = thumbUrl,
        tags = tags.map { it.toDomain() }.toSet()
    )

    companion object {
        fun fromDomain(product: Product) = ProductDto(
            id = product.id,
            name = product.name,
            ean = product.ean?.let { EAN(it) },
            imageUrl = product.imageUrl,
            thumbUrl = product.thumbUrl,
            tags = product.tags.map { TagDto.fromDomain(it) }.toSet()
        )
    }
}
