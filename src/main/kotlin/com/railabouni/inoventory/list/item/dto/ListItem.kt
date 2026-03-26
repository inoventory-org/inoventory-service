package com.railabouni.inoventory.list.item.dto

import com.railabouni.inoventory.list.entity.InventoryListEntity
import com.railabouni.inoventory.list.item.entity.ListItemEntity
import com.railabouni.inoventory.product.dto.Product
import java.time.LocalDate

data class ListItem(
    val id: Long? = null,
    val displayName: String? = null,
    val expirationDate: LocalDate?,
    val productEan: String,
    val imageUrl: String? = null,
    val thumbUrl: String? = null,
    val listId: Long,
) {

    fun toEntity(product: Product, list: InventoryListEntity) = ListItemEntity(
        id = id,
        expirationDate = expirationDate,
        productEan = product.ean.value,
        productName = product.name,
        productBrands = product.brands,
        productImageUrl = product.imageUrl,
        productThumbUrl = product.thumbUrl,
        list = list
    )

    companion object {
        fun fromEntity(listItem: ListItemEntity) = ListItem(
            id = listItem.id,
            expirationDate = listItem.expirationDate,
            displayName = listItem.productBrands?.let { "$it ${listItem.productName}" } ?: listItem.productName,
            productEan = listItem.productEan,
            imageUrl = listItem.productImageUrl,
            thumbUrl = listItem.productThumbUrl,
            listId = listItem.list.id!!
        )
    }
}
