package com.inovex.inoventory.list

import com.inovex.inoventory.exceptions.ResourceNotFoundException
import com.inovex.inoventory.list.domain.InventoryList
import org.springframework.stereotype.Service

@Service
class InventoryListServiceImpl(private val inventoryListRepository: InventoryListRepository) : InventoryListService {

    override fun getAll(): List<InventoryList> {
        return inventoryListRepository.findAll()
    }

    override fun getById(id: Long): InventoryList {
        return inventoryListRepository.findById(id).orElseThrow {
            ResourceNotFoundException("InventoryList with id $id not found")
        }
    }

    override fun create(inventoryList: InventoryList): InventoryList {
        return inventoryListRepository.save(inventoryList)
    }

    override fun update(id: Long, inventoryList: InventoryList): InventoryList {
        val existingList = inventoryListRepository.findById(id).orElseThrow {
            ResourceNotFoundException("InventoryList with id $id not found")
        }
        val updatedList = existingList.copy(name = inventoryList.name, user = inventoryList.user)
        return inventoryListRepository.save(updatedList)
    }

    override fun delete(id: Long) {
        inventoryListRepository.deleteById(id)
    }
}
