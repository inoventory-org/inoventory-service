package com.inovex.inoventory.list.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import com.inovex.inoventory.user.entity.UserEntity
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
data class InventoryListEntity (
    @Id
    @GeneratedValue
    val id: Long? = null,
    val name: String,

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    val user: UserEntity
)