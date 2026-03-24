package com.inovex.inoventory.product.entity

import com.inovex.inoventory.product.tag.entity.TagEntity
import jakarta.persistence.*
import java.time.Instant

@Entity
data class ProductEntity(
    @Id
    val ean: String,
    val name: String,
    val brands: String? = null,
    val source: SourceEntity,
    val imageUrl: String? = null,
    val thumbUrl: String? = null,
    val cachedTimestamp: Instant? = null,

    @ManyToMany(cascade = [CascadeType.ALL, CascadeType.MERGE])
    val tags: List<TagEntity> = listOf(),
)
