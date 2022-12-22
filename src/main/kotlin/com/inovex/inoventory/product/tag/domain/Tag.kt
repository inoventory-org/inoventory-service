package com.inovex.inoventory.product.tag.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.inovex.inoventory.product.domain.Product
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany

@Entity
data class Tag(
        @Id
        @GeneratedValue
        val id: Long? = null,
        val name: String,


        @ManyToMany
        @JsonIgnore
        val products: Set<Product> = setOf()
) {
        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as Tag

                if (name != other.name) return false

                return true
        }

        override fun hashCode(): Int {
                return name.hashCode()
        }
}