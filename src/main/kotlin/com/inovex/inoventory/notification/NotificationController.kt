package com.inovex.inoventory.notification

import com.inovex.inoventory.list.dto.InventoryList
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/inventory-lists/{id}/notifications")
class NotificationController(private val notificationService: NotificationService) {

    @GetMapping
    fun getAll(@PathVariable id: Long) = notificationService.getAll(id)

}