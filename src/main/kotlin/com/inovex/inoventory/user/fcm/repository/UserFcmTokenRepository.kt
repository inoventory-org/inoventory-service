package com.inovex.inoventory.user.fcm.repository

import com.inovex.inoventory.user.fcm.entity.UserFcmTokenEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserFcmTokenRepository : JpaRepository<UserFcmTokenEntity, UUID>
