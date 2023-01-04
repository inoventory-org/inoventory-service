package com.inovex.inoventory.list.permission.dto

import com.inovex.inoventory.list.permission.entity.AccessRight
import com.inovex.inoventory.list.permission.entity.PermissionEntity
import java.util.*

data class Permission(
    val listId: Long,
    val userId: UUID,
    val accessRight: AccessRight
){
    fun toEntity() = PermissionEntity(
        listId = listId,
        userId = userId,
        accessRight = accessRight
    )

    companion object{
        fun fromEntity(entity: PermissionEntity) = Permission(
            listId = entity.listId,
            userId = entity.userId,
            accessRight = entity.accessRight
        )
    }
}