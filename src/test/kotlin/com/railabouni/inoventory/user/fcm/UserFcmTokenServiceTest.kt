package com.railabouni.inoventory.user.fcm

import com.railabouni.inoventory.user.fcm.entity.UserFcmTokenEntity
import com.railabouni.inoventory.user.fcm.repository.UserFcmTokenRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.Optional
import java.util.UUID

class UserFcmTokenServiceTest {

    private val repository: UserFcmTokenRepository = mockk()
    private val service = UserFcmTokenService(repository)

    @Test
    fun `saveToken should save new token if none exists`() {
        val userId = UUID.randomUUID()
        val token = "new-token-123"

        every { repository.findById(userId) } returns Optional.empty()
        
        // Let it return a dummy argument on save
        every { repository.save(any()) } answers { firstArg() as UserFcmTokenEntity }

        service.saveToken(userId, token)

        verify {
            repository.save(withArg {
                assertEquals(userId, it.userId)
                assertEquals(token, it.token)
            })
        }
    }

    @Test
    fun `saveToken should update existing token`() {
        val userId = UUID.randomUUID()
        val oldToken = "old-token-abc"
        val newToken = "new-token-123"
        val existingEntity = UserFcmTokenEntity(userId, oldToken)

        every { repository.findById(userId) } returns Optional.of(existingEntity)
        every { repository.save(any()) } answers { firstArg() as UserFcmTokenEntity }

        service.saveToken(userId, newToken)

        verify {
            repository.save(withArg {
                assertEquals(userId, it.userId)
                assertEquals(newToken, it.token)
            })
        }
    }

    @Test
    fun `getToken should return token if exists`() {
        val userId = UUID.randomUUID()
        val token = "my-token"
        
        every { repository.findById(userId) } returns Optional.of(UserFcmTokenEntity(userId, token))

        val result = service.getToken(userId)
        assertEquals(token, result)
    }

    @Test
    fun `getToken should return null if none exists`() {
        val userId = UUID.randomUUID()
        
        every { repository.findById(userId) } returns Optional.empty()

        val result = service.getToken(userId)
        assertEquals(null, result)
    }
}
