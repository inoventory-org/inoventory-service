package com.railabouni.inoventory.list.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Column
import java.util.UUID

@Entity
data class InventoryListEntity (
    @Id
    @GeneratedValue
    val id: Long? = null,
    val name: String,

    @Column(name = "user_id", nullable = false)
    val userId: UUID
)
