package com.inovex.inoventory.list.service

import com.inovex.inoventory.list.domain.InventoryList

interface InventoryListService {
    fun getAll(): List<InventoryList>
    fun getById(id: Long): InventoryList
    fun create(inventoryList: InventoryList): InventoryList
    fun update(id: Long, inventoryList: InventoryList): InventoryList
    fun delete(id: Long)
}
