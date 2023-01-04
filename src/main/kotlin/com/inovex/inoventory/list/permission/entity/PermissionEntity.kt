package com.inovex.inoventory.list.permission.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import java.util.*

@Entity
data class PermissionEntity(
    @Id
    @GeneratedValue
    var id: Long? = null,
    val listId: Long,
    val userId: UUID,
    val accessRight: AccessRight
)