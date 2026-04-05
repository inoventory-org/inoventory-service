import com.railabouni.inoventory.exceptions.ResourceNotFoundException
import com.railabouni.inoventory.list.InventoryListRepository
import com.railabouni.inoventory.list.dto.InventoryList
import com.railabouni.inoventory.list.dto.ReorderInventoryListsRequest
import com.railabouni.inoventory.list.entity.InventoryListEntity
import com.railabouni.inoventory.list.InventoryListService
import com.railabouni.inoventory.list.permission.PermissionService
import com.railabouni.inoventory.list.permission.dto.Permission
import com.railabouni.inoventory.list.permission.entity.AccessRight
import com.railabouni.inoventory.user.dto.UserDto
import com.railabouni.inoventory.user.service.CurrentUserService
import com.railabouni.inoventory.config.DbAuthContext
import io.mockk.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.repository.findByIdOrNull
import java.util.*


class InventoryListServiceTests {

    private val inventoryListRepository = mockk<InventoryListRepository>()
    private val currentUserService = mockk<CurrentUserService>()
    private val permissionService = mockk<PermissionService>()
    private val dbAuthContext = mockk<DbAuthContext>(relaxed = true)
    private val inventoryListService =
        InventoryListService(inventoryListRepository, currentUserService, permissionService, dbAuthContext)

    @Test
    fun `getAll should return all lists from the repository`() {
        // Given
        val userId = UUID.randomUUID()
        val user = UserDto(id = userId, userName = "luke.skywalker")
        val list1 = InventoryListEntity(id = 1L, name = "List 1", userId = userId, sortOrder = 1)
        val list2 = InventoryListEntity(id = 2L, name = "List 2", userId = userId, sortOrder = 0)
        val lists = listOf(list1, list2)
        every { inventoryListRepository.findAllByIdIn(lists.map { it.id!! }) } returns lists
        every { currentUserService.getCurrentUser() } returns user
        every { permissionService.getByUserIdAndAccessRight(userId, AccessRight.READ) } returns lists.map {
            Permission(
                it.id!!,
                userId,
                AccessRight.READ
            )
        }

        // When
        val result = inventoryListService.getAll()

        // Then
        assertEquals(listOf(list2, list1), result)
    }

    @Test
    fun `getById should return the list with the given id`() {
        // Given
        val userId = UUID.randomUUID()
        val user = UserDto(id = userId, userName = "luke.skywalker")
        val id = 1L
        val list = InventoryList(id = id, name = "List 1", userId = userId)
        every { inventoryListRepository.findByIdOrNull(id) } returns list.toEntity(userId)
        every { currentUserService.getCurrentUser() } returns user
        every { permissionService.userCanAccessList(userId, id) } returns true

        // When
        val result = inventoryListService.getById(id)

        // Then
        assertEquals(list, result)
        verify { inventoryListRepository.findById(id) }
    }

    @Test
    fun `getById should throw ResourceNotFoundException when list is not found`() {
        // Given
        val user = UserDto(id = UUID.randomUUID(), userName = "luke.skywalker")
        val id = 1L
        every { inventoryListRepository.findById(id) } returns Optional.empty()
        every { currentUserService.getCurrentUser() } returns user

        // When
        val exception = assertThrows<ResourceNotFoundException> {
            inventoryListService.getById(id)
        }

        // Then
        assertEquals("InventoryList with id $id not found", exception.message)
        verify { inventoryListRepository.findById(id) }
    }


    @Test
    fun `create should save the new list and return it`() {
        // Given
        val userId = UUID.randomUUID()
        val user = UserDto(id = userId, userName = "luke.skywalker")
        val list = InventoryList(name = "List 1")
        val saved = InventoryListEntity(id = 1L, name = "List 1", userId = userId, sortOrder = 0)
        every { inventoryListRepository.findAllByIdIn(any()) } returns emptyList()
        every { permissionService.getByUserIdAndAccessRight(userId, AccessRight.READ) } returns emptyList()
        every { inventoryListRepository.save(match { it.name == "List 1" && it.sortOrder == 0 }) } returns saved
        every { currentUserService.getCurrentUser() } returns user
        every { permissionService.createPermissions(userId, saved.id!!, any()) } just runs

        // When
        val result = inventoryListService.create(list)

        // Then
        assertEquals(InventoryList.fromEntity(saved), result)
        verify { inventoryListRepository.save(match { it.name == "List 1" && it.sortOrder == 0 }) }
    }

    @Test
    fun `update should save the updated list and return it`() {
        // Given
        val userId = UUID.randomUUID()
        val user = UserDto(id = userId, userName = "luke.skywalker")
        val id = 1L
        val list = InventoryList(id = id, name = "List 1", userId = userId)
        val updatedList = list.copy(name = "Updated List")
        every { inventoryListRepository.findByIdOrNull(id) } returns list.toEntity(userId)
        every { inventoryListRepository.save(match { it.name == "Updated List" }) } returns updatedList.toEntity(userId)
        every { currentUserService.getCurrentUser() } returns user
        every { permissionService.userCanEditList(userId, id) } returns true
        every { permissionService.userCanAccessList(userId, id) } returns true

        // When
        val result = inventoryListService.update(id, updatedList)

        // Then
        assertEquals(updatedList, result)
    }

    @Test
    fun `delete should delete the list with the given id`() {
        // Given
        val userId = UUID.randomUUID()
        val user = UserDto(id = userId, userName = "luke.skywalker")
        val id = 1L
        val list = InventoryList(id = id, name = "List 1", userId = userId)
        val localInventoryListRepository = mockk<InventoryListRepository>(relaxed = true)
        val localInventoryListService =
            InventoryListService(localInventoryListRepository, currentUserService, permissionService, dbAuthContext)

        every { localInventoryListRepository.deleteById(id) } returns Unit
        every { localInventoryListRepository.findByIdOrNull(id) } returns list.toEntity(userId)
        every { currentUserService.getCurrentUser() } returns user
        every { permissionService.userCanDeleteList(userId, id) } returns true
        every { permissionService.userCanAccessList(userId, id) } returns true
        // When
        localInventoryListService.delete(id)

        // Then
        verify { localInventoryListRepository.deleteById(id) }
    }

    @Test
    fun `reorder should persist the provided list order`() {
        val userId = UUID.randomUUID()
        val user = UserDto(id = userId, userName = "luke.skywalker")
        val list1 = InventoryListEntity(id = 1L, name = "Kitchen", userId = userId, sortOrder = 0)
        val list2 = InventoryListEntity(id = 2L, name = "Open", userId = userId, sortOrder = 1)
        val request = ReorderInventoryListsRequest(listOf(2L, 1L))

        every { currentUserService.getCurrentUser() } returns user
        every { permissionService.getByUserIdAndAccessRight(userId, AccessRight.READ) } returns listOf(
            Permission(1L, userId, AccessRight.READ),
            Permission(2L, userId, AccessRight.READ)
        )
        every { inventoryListRepository.findAllByIdIn(listOf(1L, 2L)) } returns listOf(list1, list2)
        every { permissionService.userCanEditList(userId, 1L) } returns true
        every { permissionService.userCanEditList(userId, 2L) } returns true
        every { inventoryListRepository.save(any()) } answers { firstArg() }

        val result = inventoryListService.reorder(request)

        assertEquals(listOf(2L, 1L), result.map { it.id })
        assertEquals(listOf(0, 1), result.map { it.sortOrder })
    }
}
