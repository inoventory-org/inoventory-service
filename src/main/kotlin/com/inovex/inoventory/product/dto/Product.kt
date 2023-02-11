package com.inovex.inoventory.product.dto

import com.inovex.inoventory.product.entity.ProductEntity
import com.inovex.inoventory.product.entity.SourceEntity
import com.inovex.inoventory.product.tag.dto.Tag
import java.time.Instant

data class Product(
    val ean: EAN,
    val name: String,
    val brands: String? = null,
    val imageUrl: String? = null,
    val thumbUrl: String? = null,
    var tags: List<Tag> = listOf()
) {
    fun toEntity(source: SourceEntity, cacheTime: Instant?) = ProductEntity(
        ean = ean.value,
        name = name,
        brands = brands,
        source = source,
        imageUrl = imageUrl,
        thumbUrl = thumbUrl,
        cachedTimestamp = cacheTime,
        tags = tags.map { it.toEntity() }
    )

    companion object {
        fun fromEntity(product: ProductEntity) = Product(
            ean = EAN(product.ean),
            name = product.name,
            brands = product.brands,
            imageUrl = product.imageUrl,
            thumbUrl = product.thumbUrl,
            tags = product.tags.map { Tag.fromEntity(it) }
        )
    }
}
