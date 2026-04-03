package com.railabouni.inoventory.user.fcm.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "user_fcm_token")
data class UserFcmTokenEntity(
    @Id
    val userId: UUID,
    var token: String
)
