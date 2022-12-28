package com.inovex.inoventory.product.domain

import com.inovex.inoventory.list.item.domain.ListItem
import com.inovex.inoventory.product.tag.domain.Tag
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany
import jakarta.persistence.OneToMany

@Entity
data class Product(
        @Id
        @GeneratedValue
        val id: Long? = null,
        val ean: String,
        val name: String,
        val brands: String? = null,
        val source: Source,
        val imageUrl: String? = null,
        val thumbUrl: String? = null,

        @OneToMany(mappedBy = "product", cascade = [CascadeType.ALL])
        val listItems: List<ListItem> = listOf(),

        @ManyToMany(cascade = [CascadeType.ALL, CascadeType.MERGE])
        val tags: Set<Tag> = setOf(),

        // TODO: add caching time
        // When looking up the product, the service could refresh it if its older than a day for example

)
