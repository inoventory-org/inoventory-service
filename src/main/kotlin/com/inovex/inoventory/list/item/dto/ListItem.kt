package com.inovex.inoventory.list.item.dto

import com.inovex.inoventory.list.entity.InventoryListEntity
import com.inovex.inoventory.list.item.entity.ListItemEntity
import com.inovex.inoventory.product.entity.ProductEntity
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

    fun toEntity(product: ProductEntity, list: InventoryListEntity) = ListItemEntity(
        id = id,
        expirationDate = expirationDate,
        product = product,
        list = list
    )

    companion object {
        fun fromEntity(listItem: ListItemEntity) = ListItem(
            id = listItem.id,
            expirationDate = listItem.expirationDate,
            displayName = listItem.product.brands?.let { "$it ${listItem.product.name}" } ?: listItem.product.name,
            productEan = listItem.product.ean,
            imageUrl = listItem.product.imageUrl,
            thumbUrl = listItem.product.thumbUrl,
            listId = listItem.list.id!!
        )
    }
}
