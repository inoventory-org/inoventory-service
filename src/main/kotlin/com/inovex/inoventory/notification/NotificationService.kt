package com.inovex.inoventory.notification

import com.inovex.inoventory.exceptions.NotAuthorizedException
import com.inovex.inoventory.list.permission.PermissionService
import com.inovex.inoventory.notification.dto.Notification
import com.inovex.inoventory.user.service.UserService
import org.springframework.stereotype.Service

@Service
class NotificationService(
    private val notificationRepository: NotificationRepository,
    private val userService: UserService,
    private val permissionService: PermissionService
) {
    fun getAll(listId: Long): List<Notification> {
        val userId = userService.getAuthenticatedUser().id
        if (!permissionService.userCanAccessList(userId, listId))
            throw NotAuthorizedException("User $userId is not allowed to access list $listId")

        return notificationRepository.findGroupedNotificationsByListId(listId).map { Notification.fromEntity(it, listId) }
    }

}
