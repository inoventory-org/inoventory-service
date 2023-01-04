package com.inovex.inoventory.list.permission

import com.inovex.inoventory.list.permission.entity.AccessRight
import com.inovex.inoventory.list.permission.entity.PermissionEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface PermissionRepository : JpaRepository<PermissionEntity, Long> {
    fun findByUserIdAndAccessRight(userId: UUID, accessRight: AccessRight): List<PermissionEntity>
    fun findByListId(listId: Long): List<PermissionEntity>
    fun findByUserIdAndListId(userId: UUID, listId: Long): List<PermissionEntity>
}