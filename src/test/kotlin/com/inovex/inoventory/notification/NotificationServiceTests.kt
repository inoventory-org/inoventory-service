import com.inovex.inoventory.exceptions.NotAuthorizedException
import com.inovex.inoventory.list.permission.PermissionService
import com.inovex.inoventory.notification.NotificationRepository
import com.inovex.inoventory.notification.NotificationService
import com.inovex.inoventory.notification.dto.Notification
import com.inovex.inoventory.notification.dto.NotificationQueryResult
import com.inovex.inoventory.user.dto.UserDto
import com.inovex.inoventory.user.entity.UserEntity
import com.inovex.inoventory.user.service.UserService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate

class NotificationServiceTests {

    private val notificationRepository = mockk<NotificationRepository>()
    private val userService = mockk<UserService>()
    private val permissionService = mockk<PermissionService>()
    private val notificationService = NotificationService(notificationRepository, userService, permissionService)

    @Test
    fun `getAll should return all notifications for given list when user has access`() {
        // Given
        val user = UserEntity(userName = "luke.skywalker")
        val listId = 1L

        every { userService.getAuthenticatedUser() } returns UserDto.fromEntity(user)
        every { permissionService.userCanAccessList(user.id, listId) } returns true

        val date1 = LocalDate.of(2025, 2, 22)
        val date2 = LocalDate.of(2025, 3, 28)
        every { notificationRepository.findGroupedNotificationsByListId(listId) } returns listOf(
            NotificationQueryResult(date1, 3L),
            NotificationQueryResult(date2, 5L)
        )
        val expected = listOf(
            Notification(202502221, 3L, date1),
            Notification(202503281, 5L, date2),
        )

        // When
        val result = notificationService.getAll(listId)

        // Then
        assertEquals(expected, result)
    }

    @Test
    fun `getAll throws error when user not authorized to access list`() {
        // Given
        val user = UserEntity(userName = "luke.skywalker")
        val listId = 1L

        every { userService.getAuthenticatedUser() } returns UserDto.fromEntity(user)
        every { permissionService.userCanAccessList(user.id, listId) } returns false

        // When & then
        assertThrows<NotAuthorizedException> {
            notificationService.getAll(listId)
        }
    }
}