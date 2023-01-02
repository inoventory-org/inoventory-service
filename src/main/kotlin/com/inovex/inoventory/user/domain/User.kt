package com.inovex.inoventory.user.domain

import com.inovex.inoventory.list.domain.InventoryList
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "users")
data class User(
    @Id
    val id: UUID = UUID.randomUUID(),
    val userName: String,

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    val lists: List<InventoryList> = listOf()
)