package com.inovex.inoventory.list

import com.inovex.inoventory.list.domain.InventoryList
import com.inovex.inoventory.list.service.InventoryListService
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

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = inventoryListService.delete(id)
}