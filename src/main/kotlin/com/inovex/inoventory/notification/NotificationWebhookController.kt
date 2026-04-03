package com.inovex.inoventory.notification

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/jobs")
class NotificationWebhookController(
    private val notificationWebhookService: NotificationWebhookService
) {

    @Value("\${app.cron-secret:random-default-secret}")
    private lateinit var validCronSecret: String

    @PostMapping("/check-expiring-items")
    fun triggerCheck(
        @RequestHeader("X-Cron-Secret", required = false) cronSecret: String?
    ): ResponseEntity<String> {
        if (cronSecret != validCronSecret) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or missing X-Cron-Secret")
        }

        val parsedLists = notificationWebhookService.triggerExpirationNotifications()
        return ResponseEntity.ok("Successfully sent notifications for $parsedLists lists.")
    }
}
