package com.railabouni.inoventory.list

import com.railabouni.inoventory.exceptions.NotAuthorizedException
import com.railabouni.inoventory.exceptions.ResourceNotFoundException
import com.railabouni.inoventory.list.dto.InventoryList
import com.railabouni.inoventory.list.dto.ReorderInventoryListsRequest
import com.railabouni.inoventory.list.entity.InventoryListEntity
import com.railabouni.inoventory.list.entity.InventoryListType
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
        return inventoryListRepository
            .findAllByIdIn(allowedLists.map { it.listId })
            .sortedWith(compareBy<InventoryListEntity> { it.sortOrder }.thenBy { it.id })
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
        if (inventoryList.type == InventoryListType.OPEN) {
            throw IllegalStateException("Open lists are managed internally.")
        }
        val nextSortOrder = getAll().maxOfOrNull { it.sortOrder }?.plus(1) ?: 0
        val listWithUser = inventoryList.copy(sortOrder = nextSortOrder).toEntity(user.id)

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
        if (existingList.type == InventoryListType.OPEN) {
            throw IllegalStateException("Open lists cannot currently be renamed.")
        }
        val updatedList = existingList.copy(name = inventoryList.name).toEntity(userId)
        return InventoryList.fromEntity(inventoryListRepository.save(updatedList))
    }

    @Transactional
    fun reorder(request: ReorderInventoryListsRequest): List<InventoryList> {
        dbAuthContext.apply()
        val userId = currentUserService.getCurrentUser().id
        val currentLists = getAll()
        val currentIds = currentLists.mapNotNull { it.id }
        if (request.listIds.toSet() != currentIds.toSet() || request.listIds.size != currentIds.size) {
            throw IllegalArgumentException("Reorder request must contain exactly the accessible list ids.")
        }

        val reordered = request.listIds.mapIndexed { index, listId ->
            if (!permissionService.userCanEditList(userId, listId)) {
                throw NotAuthorizedException("User $userId is not allowed to reorder list $listId")
            }
            val list = currentLists.first { it.id == listId }
            inventoryListRepository.save(list.copy(sortOrder = index))
        }
        return reordered
            .sortedWith(compareBy<InventoryListEntity> { it.sortOrder }.thenBy { it.id })
            .map { InventoryList.fromEntity(it) }
    }

    @Transactional
    fun delete(id: Long) {
        dbAuthContext.apply()
        val userId = currentUserService.getCurrentUser().id
        if (!permissionService.userCanDeleteList(userId, id))
            throw NotAuthorizedException("User $userId is not allowed to delete list $id")

        val existingList = getById(id)
        if (existingList.type == InventoryListType.OPEN) {
            throw IllegalStateException("Open lists cannot currently be deleted.")
        }

        inventoryListRepository.deleteById(id)
    }

    @Transactional
    fun getOrCreateOpenList(): InventoryListEntity {
        dbAuthContext.apply()
        val user = currentUserService.getCurrentUser()
        val existingOpenList = inventoryListRepository.findByUserIdAndType(user.id, InventoryListType.OPEN)
        if (existingOpenList != null) {
            return existingOpenList
        }

        val createdList = inventoryListRepository.save(
            InventoryListEntity(
                name = "Open",
                userId = user.id,
                sortOrder = inventoryListRepository.findAllByUserIdOrderBySortOrderAscIdAsc(user.id)
                    .maxOfOrNull { it.sortOrder }?.plus(1) ?: 0,
                type = InventoryListType.OPEN
            )
        )
        requireNotNull(createdList.id)
        permissionService.createPermissions(
            user.id,
            createdList.id,
            listOf(AccessRight.READ, AccessRight.WRITE, AccessRight.DELETE)
        )
        return createdList
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
