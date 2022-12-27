package com.inovex.inoventory.list.item.domain

import com.inovex.inoventory.product.domain.Product
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
data class ListItem (
    @Id
    @GeneratedValue
    val id: Long? = null,
    val name: String,
    val expirationDate: String?,

    @ManyToOne
    @JoinColumn(name = "product_id")
    val product: Product
)