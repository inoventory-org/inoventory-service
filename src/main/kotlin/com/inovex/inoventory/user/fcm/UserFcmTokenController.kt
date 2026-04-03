package com.inovex.inoventory.user.fcm

import com.inovex.inoventory.user.service.CurrentUserService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class FcmTokenRequest(val token: String)

@RestController
@RequestMapping("/api/v1/user/fcm-token")
class UserFcmTokenController(
    private val currentUserService: CurrentUserService,
    private val userFcmTokenService: UserFcmTokenService
) {
    @PostMapping
    fun saveToken(@RequestBody request: FcmTokenRequest) {
        val user = currentUserService.getCurrentUser()
        userFcmTokenService.saveToken(user.id, request.token)
    }
}
