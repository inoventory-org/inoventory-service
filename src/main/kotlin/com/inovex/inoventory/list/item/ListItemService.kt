package com.inovex.inoventory.list.item

import com.inovex.inoventory.exceptions.ResourceNotFoundException
import com.inovex.inoventory.list.InventoryListRepository
import com.inovex.inoventory.list.item.dto.ListItemDTO
import com.inovex.inoventory.product.ProductRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class ListItemService(private val repository: ListItemRepository,
                      private val productRepository: ProductRepository,
                      private val listRepository: InventoryListRepository) {
    fun getAll(listId: Long): List<ListItemDTO> {
        return repository.findAllByListId(listId).map { ListItemDTO.fromDomain(it) }
    }

    fun findOrNull(id: Long, listId: Long): ListItemDTO? {
        return repository.findByIdAndListId(id, listId)?.let{ ListItemDTO.fromDomain(it) }
    }

    fun create(listId: Long, listItemDto: ListItemDTO): ListItemDTO {
        val product = productRepository.findByEan(listItemDto.productEan)
            ?: throw ResourceNotFoundException("Product with EAN ${listItemDto.productEan} not found.")
        val list = listRepository.findByIdOrNull(listId) ?: throw ResourceNotFoundException("List with ID $listId not found.")
        val listItem = listItemDto.toDomain(product, list)
        return repository.save(listItem).let { ListItemDTO.fromDomain(it) }
    }

    fun update(id: Long, listId: Long, listItemDto: ListItemDTO): ListItemDTO {
        val existingItem = repository.findByIdAndListId(id, listId)
            ?: throw ResourceNotFoundException("ListItem with id $id not found")
        val potentialNewList =  listRepository.findByIdOrNull(listItemDto.listId)
            ?: throw ResourceNotFoundException("List with ID ${listItemDto.listId} not found.")
        val updatedItem =
            existingItem.copy(expirationDate = listItemDto.expirationDate, product = existingItem.product, list = potentialNewList)
        return repository.save(updatedItem).let { ListItemDTO.fromDomain(it) }
    }

    fun delete(id: Long) {
        repository.deleteById(id)
    }
}