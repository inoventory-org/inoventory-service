package com.inovex.inoventory.list.service

import com.inovex.inoventory.exceptions.NotAuthorizedException
import com.inovex.inoventory.exceptions.ResourceNotFoundException
import com.inovex.inoventory.list.InventoryListRepository
import com.inovex.inoventory.list.domain.InventoryList
import com.inovex.inoventory.user.service.UserService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class InventoryListService(
    private val inventoryListRepository: InventoryListRepository,
    private val userService: UserService
) {
    fun getAll(): List<InventoryList> {
        val userId = userService.getAuthenticatedUser().id
        return inventoryListRepository.findByUserId(userId)
    }

    fun getById(id: Long): InventoryList {
        val list = inventoryListRepository.findByIdOrNull(id)
            ?: throw ResourceNotFoundException("InventoryList with id $id not found")

        require(list.user != null)

        val userId = userService.getAuthenticatedUser().id
        return if (list.user.id == userId) list
        else throw NotAuthorizedException("User $userId cannot access list ${list.id}")
    }

    fun create(inventoryList: InventoryList): InventoryList {
        val listWithUser = inventoryList.copy(user = userService.getAuthenticatedUser())
        return inventoryListRepository.saveAndFlush(listWithUser)
    }

    fun update(id: Long, inventoryList: InventoryList): InventoryList {
        val existingList = getById(id)
        val updatedList = existingList.copy(name = inventoryList.name, user = inventoryList.user)
        return inventoryListRepository.save(updatedList)
    }

    fun delete(id: Long) {
        val listToDelete = getById(id) //checks if list exists and user has access
        inventoryListRepository.deleteById(id)
    }
}
