package com.railabouni.inoventory.user.fcm.repository

import com.railabouni.inoventory.user.fcm.entity.UserFcmTokenEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserFcmTokenRepository : JpaRepository<UserFcmTokenEntity, UUID>
