package com.inovex.inoventory.user.fcm

import com.inovex.inoventory.user.fcm.entity.UserFcmTokenEntity
import com.inovex.inoventory.user.fcm.repository.UserFcmTokenRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class UserFcmTokenService(
    private val repository: UserFcmTokenRepository
) {
    @Transactional
    fun saveToken(userId: UUID, token: String) {
        val entity = repository.findById(userId).orElse(UserFcmTokenEntity(userId, token))
        entity.token = token
        repository.save(entity)
    }

    fun getToken(userId: UUID): String? {
        return repository.findById(userId).map { it.token }.orElse(null)
    }
}
