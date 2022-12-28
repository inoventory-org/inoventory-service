package com.inovex.inoventory.list

import com.inovex.inoventory.list.domain.InventoryList
import com.inovex.inoventory.list.service.InventoryListService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/inventory-lists")
class InventoryListController(private val inventoryListService: InventoryListService) {

    @GetMapping
    fun getAll(): List<InventoryList> = inventoryListService.getAll()

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): InventoryList = inventoryListService.getById(id)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody inventoryList: InventoryList): InventoryList = inventoryListService.create(inventoryList)

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody inventoryList: InventoryList): InventoryList
    = inventoryListService.update(id, inventoryList)

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = inventoryListService.delete(id)
}