package com.inovex.inoventory.list.permission

import com.inovex.inoventory.list.permission.dto.Permission
import com.inovex.inoventory.list.permission.entity.AccessRight
import com.inovex.inoventory.list.permission.entity.PermissionEntity
import com.inovex.inoventory.config.DbAuthContext
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class PermissionService(
    private val repository: PermissionRepository,
    private val dbAuthContext: DbAuthContext
) {

    @Transactional
    fun createPermissions(userId: UUID, listId: Long, rights: List<AccessRight>) {
        dbAuthContext.apply()
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

    @Transactional
    fun getByUserIdAndAccessRight(userId: UUID, accessRight: AccessRight): List<Permission> =
        repository.findByUserIdAndAccessRight(userId, accessRight).map { Permission.fromEntity(it) }

    @Transactional
    fun getByListId(listId: Long): List<Permission> = repository.findByListId(listId).map { Permission.fromEntity(it) }
    @Transactional
    fun getByUserIdAndListId(userId: UUID, listId: Long): List<Permission> =
        repository.findByUserIdAndListId(userId, listId).map { Permission.fromEntity(it) }

    @Transactional
    fun userCanAccessList(userId: UUID, listId: Long) =
        getByUserIdAndListId(userId, listId).any { it.accessRight == AccessRight.READ }

    @Transactional
    fun userCanEditList(userId: UUID, listId: Long) =
        getByUserIdAndListId(userId, listId).any { it.accessRight == AccessRight.WRITE }

    @Transactional
    fun userCanDeleteList(userId: UUID, listId: Long) =
        getByUserIdAndListId(userId, listId).any { it.accessRight == AccessRight.DELETE }

}
