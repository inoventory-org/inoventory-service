package com.inovex.inoventory.list.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.inovex.inoventory.user.domain.User
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
data class InventoryList (
    @Id
    @GeneratedValue
    val id: Long? = null,
    val name: String,

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    val user: User
)