package com.inovex.inoventory.list

import com.inovex.inoventory.exceptions.NotAuthorizedException
import com.inovex.inoventory.exceptions.ResourceNotFoundException
import com.inovex.inoventory.list.dto.InventoryList
import com.inovex.inoventory.list.entity.InventoryListEntity
import com.inovex.inoventory.list.permission.PermissionService
import com.inovex.inoventory.list.permission.entity.AccessRight
import com.inovex.inoventory.user.dto.UserDto
import com.inovex.inoventory.user.service.UserService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class InventoryListService(
    private val inventoryListRepository: InventoryListRepository,
    private val userService: UserService,
    private val permissionService: PermissionService
) {
    fun getAll(): List<InventoryListEntity> {
        val userId = userService.getAuthenticatedUser().id
        val allowedLists = permissionService.getByUserIdAndAccessRight(userId, AccessRight.READ)
        return inventoryListRepository.findAllByIdIn(allowedLists.map { it.listId })
    }

    fun getById(id: Long): InventoryList {
        val list = inventoryListRepository.findByIdOrNull(id)
            ?: throw ResourceNotFoundException("InventoryList with id $id not found")

        val userId = userService.getAuthenticatedUser().id
        if (!permissionService.userCanAccessList(userId, id))
            throw NotAuthorizedException("User $userId is not allowed to access list ${list.id}")

        return InventoryList.fromEntity(list)
    }

    fun create(inventoryList: InventoryList): InventoryList {
        val user = userService.getAuthenticatedUser()
        val listWithUser = inventoryList.copy(user = user).toEntity()

        val createdList = InventoryList.fromEntity(inventoryListRepository.save(listWithUser))
        createDefaultAccessRights(user, createdList)

        return createdList
    }

    fun update(id: Long, inventoryList: InventoryList): InventoryList {
        val userId = userService.getAuthenticatedUser().id
        if (!permissionService.userCanEditList(userId, id))
            throw NotAuthorizedException("User $userId is not allowed to edit list $id")

        val existingList = getById(id)
        val updatedList = existingList.copy(name = inventoryList.name).toEntity()
        return InventoryList.fromEntity(inventoryListRepository.save(updatedList))
    }

    fun delete(id: Long) {
        val userId = userService.getAuthenticatedUser().id
        if (!permissionService.userCanDeleteList(userId, id))
            throw NotAuthorizedException("User $userId is not allowed to delete list $id")

        inventoryListRepository.deleteById(id)
    }

    private fun createDefaultAccessRights(user: UserDto, createdList: InventoryList) {
        require(createdList.id != null)

        permissionService.createPermissions(
            user.id,
            createdList.id,
            listOf(AccessRight.READ, AccessRight.WRITE, AccessRight.DELETE)
        )
    }

}
