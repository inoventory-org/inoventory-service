package com.inovex.inoventory.list.item.dto

import com.inovex.inoventory.list.domain.InventoryList
import com.inovex.inoventory.list.item.domain.ListItem
import com.inovex.inoventory.product.domain.Product

data class ListItemDTO(
    val id: Long? = null,
    val expirationDate: String?,
    val productEan: String,
    val listId: Long,
    ) {

    fun toDomain(product: Product, list: InventoryList) = ListItem(
        id = id,
        expirationDate = expirationDate,
        product = product,
        list = list
    )

    companion object {
        fun fromDomain(listItem: ListItem) = ListItemDTO(
            id = listItem.id,
            expirationDate = listItem.expirationDate,
            productEan = listItem.product.ean,
            listId = listItem.list.id!!
        )
    }
}
