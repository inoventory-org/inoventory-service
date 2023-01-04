package com.inovex.inoventory.user.entity

import com.inovex.inoventory.list.entity.InventoryListEntity
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "users")
data class UserEntity(
    @Id
    val id: UUID = UUID.randomUUID(),
    val userName: String,

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    val lists: List<InventoryListEntity> = listOf()
)