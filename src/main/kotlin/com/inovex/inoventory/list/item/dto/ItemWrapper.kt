package com.inovex.inoventory.list.item.dto

data class ItemWrapper(
    val productEan: String,
    val category: String?,
    val items: List<ListItem>?,
    val listId: Long,
)
