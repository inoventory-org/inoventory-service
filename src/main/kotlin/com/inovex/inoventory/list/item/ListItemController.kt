package com.inovex.inoventory.list.item

import com.inovex.inoventory.list.item.dto.ListItem
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/inventory-lists/{listId}/items")
class ListItemController(private val listItemService: ListItemService) {

    @GetMapping
    fun getAll(@PathVariable listId: Long): Map<String, List<ListItem>> = listItemService.getAll(listId)

    @GetMapping("/{id}")
    fun getById(@PathVariable listId: Long, @PathVariable id: Long): ListItem? = listItemService.findOrNull(id, listId)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@PathVariable listId: Long, @RequestBody inventoryList: ListItem): ListItem = listItemService.create(listId, inventoryList)

    @PutMapping("/{id}")
    fun update(@PathVariable listId: Long, @PathVariable id: Long, @RequestBody inventoryList: ListItem): ListItem
            = listItemService.update(id, listId, inventoryList)

    @DeleteMapping("/{id}")
    fun delete(@PathVariable listId: Long, @PathVariable id: Long) = listItemService.delete(id)
}