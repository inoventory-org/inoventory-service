package com.inovex.inoventory.list.item.domain

import com.inovex.inoventory.list.domain.InventoryList
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
    val expirationDate: String?, // TODO: replace with Date type, to allow for better queries/filtering on dates

    @ManyToOne
    @JoinColumn(name = "ean")
    val product: Product,

    @ManyToOne
    @JoinColumn(name = "list_id")
    val list: InventoryList
)