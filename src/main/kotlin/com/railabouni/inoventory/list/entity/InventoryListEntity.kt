package com.railabouni.inoventory.list.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Column
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import java.util.UUID

@Entity
data class InventoryListEntity (
    @Id
    @GeneratedValue
    val id: Long? = null,
    val name: String,

    @Column(name = "user_id", nullable = false)
    val userId: UUID,

    @Column(name = "sort_order", nullable = false)
    val sortOrder: Int = 0,

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    val type: InventoryListType = InventoryListType.REGULAR
)
