package com.inovex.inoventory.list.service

import com.inovex.inoventory.exceptions.NotAuthorizedException
import com.inovex.inoventory.exceptions.ResourceNotFoundException
import com.inovex.inoventory.list.InventoryListRepository
import com.inovex.inoventory.list.domain.InventoryList
import com.inovex.inoventory.user.service.UserService
import org.springframework.stereotype.Service

@Service
class InventoryListServiceImpl(private val inventoryListRepository: InventoryListRepository, private val userService: UserService) : InventoryListService {

    override fun getAll(): List<InventoryList> {
        return inventoryListRepository.findAll()
    }

    override fun getById(id: Long): InventoryList {
        return inventoryListRepository.findById(id).orElseThrow {
            ResourceNotFoundException("InventoryList with id $id not found")
        }
    }

    override fun create(inventoryList: InventoryList): InventoryList {
        return userService.getAuthenticatedUser()?.let {
            inventoryList.user = it
            inventoryListRepository.save(inventoryList)
        } ?: throw NotAuthorizedException("You must be logged in to perform this action")
    // TODO: decide on how to handle case where user is not logged in
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
