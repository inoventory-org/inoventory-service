package com.railabouni.inoventory.list.item.entity

import com.railabouni.inoventory.list.entity.InventoryListEntity
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.time.LocalDate

@Entity
data class ListItemEntity (
    @Id
    @GeneratedValue
    val id: Long? = null,
    val expirationDate: LocalDate?,
    val productEan: String,
    val productName: String,
    val productBrands: String? = null,
    val productImageUrl: String? = null,
    val productThumbUrl: String? = null,

    @ManyToOne
    @JoinColumn(name = "list_id")
    val list: InventoryListEntity
)
