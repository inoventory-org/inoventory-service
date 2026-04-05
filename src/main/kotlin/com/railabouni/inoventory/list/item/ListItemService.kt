package com.railabouni.inoventory.list.item

import com.railabouni.inoventory.exceptions.InvalidGroupByPropertyException
import com.railabouni.inoventory.exceptions.ResourceNotFoundException
import com.railabouni.inoventory.list.InventoryListRepository
import com.railabouni.inoventory.list.InventoryListService
import com.railabouni.inoventory.list.item.dto.ItemWrapper
import com.railabouni.inoventory.list.item.dto.ListItem
import com.railabouni.inoventory.list.item.dto.OpenListItemRequest
import com.railabouni.inoventory.list.entity.InventoryListType
import com.railabouni.inoventory.config.DbAuthContext
import com.railabouni.inoventory.product.ProductService
import com.railabouni.inoventory.product.dto.EAN
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class ListItemService(
    private val repository: ListItemRepository,
    private val productService: ProductService,
    private val listRepository: InventoryListRepository,
    private val inventoryListService: InventoryListService,
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
        val entity = listItem.copy(
            openedAt = when {
                listItem.openedAt != null -> listItem.openedAt
                list.type == InventoryListType.OPEN -> LocalDate.now()
                else -> null
            }
        ).toEntity(product, list)
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
                openedAt = when {
                    listItem.openedAt != null -> listItem.openedAt
                    potentialNewList.type == InventoryListType.OPEN && existingItem.openedAt == null -> LocalDate.now()
                    potentialNewList.type != InventoryListType.OPEN -> null
                    else -> existingItem.openedAt
                },
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
    fun open(id: Long, listId: Long, request: OpenListItemRequest): ListItem {
        dbAuthContext.apply()
        val existingItem = repository.findByIdOrNull(id)
            ?: throw ResourceNotFoundException("ListItem with id $id not found")
        if (existingItem.list.id != listId) {
            throw ResourceNotFoundException("ListItem with id $id was not found in list $listId")
        }

        val openList = inventoryListService.getOrCreateOpenList()
        val updatedItem = existingItem.copy(
            list = openList,
            expirationDate = request.expirationDate ?: existingItem.expirationDate,
            openedAt = request.openedAt ?: LocalDate.now(),
            notificationSent = false
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
