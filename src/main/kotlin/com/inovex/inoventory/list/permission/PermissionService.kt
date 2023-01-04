package com.inovex.inoventory.list.permission

import com.inovex.inoventory.list.permission.dto.Permission
import com.inovex.inoventory.list.permission.entity.AccessRight
import com.inovex.inoventory.list.permission.entity.PermissionEntity
import org.springframework.stereotype.Service
import java.util.*

@Service
class PermissionService(private val repository: PermissionRepository) {

    fun createPermissions(userId: UUID, listId: Long, rights: List<AccessRight>) {
        rights.forEach {
            repository.save(
                PermissionEntity(
                    userId = userId,
                    listId = listId,
                    accessRight = it
                )
            )
        }
    }

    fun getByUserIdAndAccessRight(userId: UUID, accessRight: AccessRight): List<Permission> =
        repository.findByUserIdAndAccessRight(userId, accessRight).map { Permission.fromEntity(it) }

    fun getByListId(listId: Long): List<Permission> = repository.findByListId(listId).map { Permission.fromEntity(it) }
    fun getByUserIdAndListId(userId: UUID, listId: Long): List<Permission> =
        repository.findByUserIdAndListId(userId, listId).map { Permission.fromEntity(it) }

    fun userCanAccessList(userId: UUID, listId: Long) =
        getByUserIdAndListId(userId, listId).any { it.accessRight == AccessRight.READ }

    fun userCanEditList(userId: UUID, listId: Long) =
        getByUserIdAndListId(userId, listId).any { it.accessRight == AccessRight.WRITE }

    fun userCanDeleteList(userId: UUID, listId: Long) =
        getByUserIdAndListId(userId, listId).any { it.accessRight == AccessRight.DELETE }

}