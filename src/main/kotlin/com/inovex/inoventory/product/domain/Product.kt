package com.inovex.inoventory.product.domain

import com.inovex.inoventory.product.tag.domain.Tag
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany

@Entity
data class Product(
        @Id
        @GeneratedValue
        val id: Long? = null,
        val name: String,
        val ean: String? = null,
        val source: Source,
        val imageUrl: String? = null,
        val thumbUrl: String? = null,

        @ManyToMany(cascade = [CascadeType.ALL, CascadeType.MERGE])
        val tags: Set<Tag> = setOf()
)