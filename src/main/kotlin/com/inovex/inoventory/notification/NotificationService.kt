package com.inovex.inoventory.notification

import com.inovex.inoventory.exceptions.NotAuthorizedException
import com.inovex.inoventory.exceptions.ResourceNotFoundException
import com.inovex.inoventory.list.dto.InventoryList
import com.inovex.inoventory.list.permission.PermissionService
import com.inovex.inoventory.list.permission.entity.AccessRight
import com.inovex.inoventory.notification.dto.Notification
import com.inovex.inoventory.user.dto.UserDto
import com.inovex.inoventory.user.service.UserService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class NotificationService(
    private val notificationRepository: NotificationRepository,
    private val userService: UserService,
    private val permissionService: PermissionService
) {
    fun getAll(id: Long): List<Notification> {
        val userId = userService.getAuthenticatedUser().id
        if (!permissionService.userCanAccessList(userId, id))
            throw NotAuthorizedException("User $userId is not allowed to access list $id")

        return notificationRepository.findGroupedNotificationsByListId(id).map { Notification.fromEntity(it, id) }
    }

}
