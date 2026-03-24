package com.inovex.inoventory.list.item

import com.inovex.inoventory.exceptions.InvalidGroupByPropertyException
import com.inovex.inoventory.exceptions.ResourceNotFoundException
import com.inovex.inoventory.list.InventoryListRepository
import com.inovex.inoventory.list.item.dto.ItemWrapper
import com.inovex.inoventory.list.item.dto.ListItem
import com.inovex.inoventory.config.DbAuthContext
import com.inovex.inoventory.product.ProductService
import com.inovex.inoventory.product.dto.EAN
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ListItemService(
    private val repository: ListItemRepository,
    private val productService: ProductService,
    private val listRepository: InventoryListRepository,
    private val dbAuthContext: DbAuthContext
) {
    @Transactional
    fun getAll(listId: Long): Map<String, List<ListItem>> {
        dbAuthContext.apply()
        val items = repository.findAllByListId(listId).map { ListItem.fromEntity(it) }
        return items.groupBy { it.productEan }
    }

    @Transactional
    fun getAllGroupedBy(listId: Long, groupBy: String): Map<String, List<ItemWrapper>> {
        dbAuthContext.apply()
        if (groupBy.lowercase() != "category") {
            throw InvalidGroupByPropertyException("Currently only grouping by 'category' is supported. You provided $groupBy.")
        }
        val items = repository.findAllByListId(listId).map { ListItem.fromEntity(it) }
        val itemWrappers = items.groupBy { it.productEan }.map { (ean, items) ->
            val category = productService.scan(EAN(ean))?.tags?.firstOrNull()?.name
            ItemWrapper(ean, category, items, listId)
        }
        return itemWrappers.groupBy { it.category.toString() }
    }

    @Transactional
    fun findOrNull(id: Long, listId: Long): ListItem? {
        dbAuthContext.apply()
        return repository.findByIdOrNull(id)?.let { ListItem.fromEntity(it) }
    }

    @Transactional
    fun create(listId: Long, listItem: ListItem): ListItem {
        dbAuthContext.apply()
        val product = productService.scan(EAN(listItem.productEan))
            ?: throw ResourceNotFoundException("Product with EAN ${listItem.productEan} not found.")
        val list =
            listRepository.findByIdOrNull(listId) ?: throw ResourceNotFoundException("List with ID $listId not found.")
        val entity = listItem.toEntity(product, list)
        return repository.save(entity).let { ListItem.fromEntity(it) }
    }

    @Transactional
    fun update(id: Long, listId: Long, listItem: ListItem): ListItem {
        dbAuthContext.apply()
        val existingItem = repository.findByIdOrNull(id)
            ?: throw ResourceNotFoundException("ListItem with id $id not found")
        val potentialNewList = listRepository.findByIdOrNull(listItem.listId)
            ?: throw ResourceNotFoundException("List with ID ${listItem.listId} not found.")
        val product = productService.scan(EAN(listItem.productEan))
            ?: throw ResourceNotFoundException("Product with EAN ${listItem.productEan} not found.")
        val updatedItem =
            existingItem.copy(
                expirationDate = listItem.expirationDate,
                productEan = product.ean.value,
                productName = product.name,
                productBrands = product.brands,
                productImageUrl = product.imageUrl,
                productThumbUrl = product.thumbUrl,
                list = potentialNewList
            )
        return repository.save(updatedItem).let { ListItem.fromEntity(it) }
    }

    @Transactional
    fun delete(id: Long): ListItem? {
        dbAuthContext.apply()
        val item = findOrNull(id, -1L)
        repository.deleteById(id)
        return item
    }
}
