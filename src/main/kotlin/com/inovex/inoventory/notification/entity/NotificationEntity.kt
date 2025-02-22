package com.inovex.inoventory.notification.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import com.inovex.inoventory.list.item.entity.ListItemEntity
import com.inovex.inoventory.user.entity.UserEntity
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.time.LocalDate

@Entity
data class NotificationEntity (
    @Id
    @GeneratedValue
    val id: Long? = null,
    val date: LocalDate,

    @ManyToOne
    @JoinColumn(name = "item_id")
    @JsonIgnore
    val item: ListItemEntity
)