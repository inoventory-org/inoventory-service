package com.inovex.inoventory.list.item.entity

import com.inovex.inoventory.list.entity.InventoryListEntity
import com.inovex.inoventory.product.entity.ProductEntity
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
data class ListItemEntity (
    @Id
    @GeneratedValue
    val id: Long? = null,
    val expirationDate: String?, // TODO: replace with Date type, to allow for better queries/filtering on dates

    @ManyToOne
    @JoinColumn(name = "ean")
    val product: ProductEntity,

    @ManyToOne
    @JoinColumn(name = "list_id")
    val list: InventoryListEntity
)