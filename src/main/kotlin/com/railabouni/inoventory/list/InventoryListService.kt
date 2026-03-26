package com.railabouni.inoventory.list

import com.railabouni.inoventory.exceptions.NotAuthorizedException
import com.railabouni.inoventory.exceptions.ResourceNotFoundException
import com.railabouni.inoventory.list.dto.InventoryList
import com.railabouni.inoventory.list.entity.InventoryListEntity
import com.railabouni.inoventory.list.permission.PermissionService
import com.railabouni.inoventory.list.permission.entity.AccessRight
import com.railabouni.inoventory.user.dto.UserDto
import com.railabouni.inoventory.user.service.CurrentUserService
import com.railabouni.inoventory.config.DbAuthContext
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class InventoryListService(
    private val inventoryListRepository: InventoryListRepository,
    private val currentUserService: CurrentUserService,
    private val permissionService: PermissionService,
    private val dbAuthContext: DbAuthContext
) {
    @Transactional
    fun getAll(): List<InventoryListEntity> {
        dbAuthContext.apply()
        val userId = currentUserService.getCurrentUser().id
        val allowedLists = permissionService.getByUserIdAndAccessRight(userId, AccessRight.READ)
        return inventoryListRepository.findAllByIdIn(allowedLists.map { it.listId })
    }

    @Transactional
    fun getById(id: Long): InventoryList {
        dbAuthContext.apply()
        val list = inventoryListRepository.findByIdOrNull(id)
            ?: throw ResourceNotFoundException("InventoryList with id $id not found")

        val userId = currentUserService.getCurrentUser().id
        if (!permissionService.userCanAccessList(userId, id))
            throw NotAuthorizedException("User $userId is not allowed to access list ${list.id}")

        return InventoryList.fromEntity(list)
    }

    @Transactional
    fun create(inventoryList: InventoryList): InventoryList {
        dbAuthContext.apply()
        val user = currentUserService.getCurrentUser()
        val listWithUser = inventoryList.toEntity(user.id)

        val createdList = InventoryList.fromEntity(inventoryListRepository.save(listWithUser))
        createDefaultAccessRights(user, createdList)

        return createdList
    }

    @Transactional
    fun update(id: Long, inventoryList: InventoryList): InventoryList {
        dbAuthContext.apply()
        val userId = currentUserService.getCurrentUser().id
        if (!permissionService.userCanEditList(userId, id))
            throw NotAuthorizedException("User $userId is not allowed to edit list $id")

        val existingList = getById(id)
        val updatedList = existingList.copy(name = inventoryList.name).toEntity(userId)
        return InventoryList.fromEntity(inventoryListRepository.save(updatedList))
    }

    @Transactional
    fun delete(id: Long) {
        dbAuthContext.apply()
        val userId = currentUserService.getCurrentUser().id
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
