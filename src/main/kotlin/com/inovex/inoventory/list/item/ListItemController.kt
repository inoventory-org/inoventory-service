package com.inovex.inoventory.list.item

import com.inovex.inoventory.list.item.dto.ListItemDTO
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/inventory-lists/{listId}/items")
class ListItemController(private val listItemService: ListItemService) {

    @GetMapping
    fun getAll(@PathVariable listId: Long): List<ListItemDTO> = listItemService.getAll(listId)

    @GetMapping("/{id}")
    fun getById(@PathVariable listId: Long, @PathVariable id: Long): ListItemDTO? = listItemService.findOrNull(id, listId)

    @PostMapping
    fun create(@PathVariable listId: Long, @RequestBody inventoryList: ListItemDTO): ListItemDTO = listItemService.create(listId, inventoryList)

    @PutMapping("/{id}")
    fun update(@PathVariable listId: Long, @PathVariable id: Long, @RequestBody inventoryList: ListItemDTO): ListItemDTO
            = listItemService.update(id, listId, inventoryList)

    @DeleteMapping("/{id}")
    fun delete(@PathVariable listId: Long, @PathVariable id: Long) = listItemService.delete(id)
}