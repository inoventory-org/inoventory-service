package com.inovex.inoventory.product.dto

import com.inovex.inoventory.product.entity.ProductEntity
import com.inovex.inoventory.product.entity.SourceEntity
import com.inovex.inoventory.product.tag.dto.Tag
import java.time.Instant

data class Product(
    val id: Long? = null,
    val name: String,
    val ean: EAN,
    val brands: String? = null,
    val imageUrl: String? = null,
    val thumbUrl: String? = null,
    val tags: Set<Tag> = setOf()
) {
    fun toEntity(source: SourceEntity, cacheTime: Instant?) = ProductEntity(
        id = id,
        name = name,
        brands = brands,
        ean = ean.value,
        source = source,
        imageUrl = imageUrl,
        thumbUrl = thumbUrl,
        cachedTimestamp = cacheTime,
        tags = tags.map { it.toEntity() }.toSet()
    )

    companion object {
        fun fromEntity(product: ProductEntity) = Product(
            id = product.id,
            name = product.name,
            ean = EAN(product.ean),
            brands = product.brands,
            imageUrl = product.imageUrl,
            thumbUrl = product.thumbUrl,
            tags = product.tags.map { Tag.fromEntity(it) }.toSet()
        )
    }
}
