package com.inovex.inoventory.list.item

import com.inovex.inoventory.exceptions.InvalidGroupByPropertyException
import com.inovex.inoventory.exceptions.ResourceNotFoundException
import com.inovex.inoventory.list.InventoryListRepository
import com.inovex.inoventory.list.item.dto.ItemWrapper
import com.inovex.inoventory.list.item.dto.ListItem
import com.inovex.inoventory.product.ProductRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class ListItemService(
    private val repository: ListItemRepository,
    private val productRepository: ProductRepository,
    private val listRepository: InventoryListRepository
) {
    fun getAll(listId: Long): Map<String, List<ListItem>> {
        val items = repository.findAllByListId(listId).map { ListItem.fromEntity(it) }
        return items.groupBy { it.productEan }
    }

    fun getAllGroupedBy(listId: Long, groupBy: String): Map<String, List<ItemWrapper>> {
        if (groupBy.lowercase() != "category") {
            throw InvalidGroupByPropertyException("Currently only grouping by 'category' is supported. You provided $groupBy.")
        }
        val items = repository.findAllByListId(listId).map { ListItem.fromEntity(it) }
        val itemWrappers = items.groupBy { it.productEan }.map { (ean, items) ->
            ItemWrapper(ean, productRepository.findByEan(ean)?.tags?.firstOrNull()?.name, items, listId)
        }
        return itemWrappers.groupBy { it.category.toString() }
    }

    fun findOrNull(id: Long, listId: Long): ListItem? {
        return repository.findByIdOrNull(id)?.let { ListItem.fromEntity(it) }
    }

    fun create(listId: Long, listItem: ListItem): ListItem {
        val product = productRepository.findByEan(listItem.productEan)
            ?: throw ResourceNotFoundException("Product with EAN ${listItem.productEan} not found.")
        val list =
            listRepository.findByIdOrNull(listId) ?: throw ResourceNotFoundException("List with ID $listId not found.")
        val entity = listItem.toEntity(product, list)
        return repository.save(entity).let { ListItem.fromEntity(it) }
    }

    fun update(id: Long, listId: Long, listItem: ListItem): ListItem {
        val existingItem = repository.findByIdOrNull(id)
            ?: throw ResourceNotFoundException("ListItem with id $id not found")
        val potentialNewList = listRepository.findByIdOrNull(listItem.listId)
            ?: throw ResourceNotFoundException("List with ID ${listItem.listId} not found.")
        val updatedItem =
            existingItem.copy(
                expirationDate = listItem.expirationDate,
                product = existingItem.product,
                list = potentialNewList
            )
        return repository.save(updatedItem).let { ListItem.fromEntity(it) }
    }

    fun delete(id: Long): ListItem? {
        val item = findOrNull(id, -1L)
        repository.deleteById(id)
        return item
    }
}