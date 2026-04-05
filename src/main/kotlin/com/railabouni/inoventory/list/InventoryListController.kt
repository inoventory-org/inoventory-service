package com.railabouni.inoventory.list

import com.railabouni.inoventory.list.dto.InventoryList
import com.railabouni.inoventory.list.dto.ReorderInventoryListsRequest
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/inventory-lists")
class InventoryListController(private val inventoryListService: InventoryListService) {

    @GetMapping
    fun getAll() = inventoryListService.getAll()

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long) = inventoryListService.getById(id)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody inventoryList: InventoryList) = inventoryListService.create(inventoryList)

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody inventoryList: InventoryList) =
        inventoryListService.update(id, inventoryList)

    @PostMapping("/reorder")
    fun reorder(@RequestBody request: ReorderInventoryListsRequest) =
        inventoryListService.reorder(request)

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = inventoryListService.delete(id)
}
