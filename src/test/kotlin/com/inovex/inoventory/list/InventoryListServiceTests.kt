import com.inovex.inoventory.exceptions.ResourceNotFoundException
import com.inovex.inoventory.list.InventoryListRepository
import com.inovex.inoventory.list.entity.InventoryListEntity
import com.inovex.inoventory.list.dto.InventoryList
import com.inovex.inoventory.list.InventoryListService
import com.inovex.inoventory.user.entity.UserEntity
import com.inovex.inoventory.user.dto.UserDto
import com.inovex.inoventory.user.service.UserService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.repository.findByIdOrNull
import java.util.*

class InventoryListServiceTests {

    private val inventoryListRepository = mockk<InventoryListRepository>()
    private val userService = mockk<UserService>()
    private val inventoryListService = InventoryListService(inventoryListRepository, userService)

    @Test
    fun `getAll should return all lists from the repository`() {
        // Given
        val user = UserEntity(userName = "luke.skywalker")
        val list1 = InventoryListEntity(name = "List 1", user = user)
        val list2 = InventoryListEntity(name = "List 2", user = user)
        val lists = listOf(list1, list2)
        every { inventoryListRepository.findByUserId(user.id) } returns lists
        every { userService.getAuthenticatedUser() } returns UserDto.fromEntity(user)

        // When
        val result = inventoryListService.getAll()

        // Then
        assertEquals(lists, result)
    }

    @Test
    fun `getById should return the list with the given id`() {
        // Given
        val user = UserEntity(userName = "luke.skywalker")
        val id = 1L
        val list = InventoryList(id = id, name = "List 1", UserDto.fromEntity(user))
        every { inventoryListRepository.findByIdOrNull(id) } returns list.toEntity()
        every { userService.getAuthenticatedUser() } returns UserDto.fromEntity(user)

        // When
        val result = inventoryListService.getById(id)

        // Then
        assertEquals(list, result)
        verify { inventoryListRepository.findById(id) }
    }

    @Test
    fun `getById should throw ResourceNotFoundException when list is not found`() {
        // Given
        val user = UserEntity(userName = "luke.skywalker")
        val id = 1L
        every { inventoryListRepository.findById(id) } returns Optional.empty()
        every { userService.getAuthenticatedUser() } returns UserDto.fromEntity(user)

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
        val user = UserEntity(userName = "luke.skywalker")
        val list = InventoryList(name = "List 1", user = UserDto.fromEntity(user))
        every { inventoryListRepository.save(list.toEntity()) } returns list.toEntity()
        every { userService.getAuthenticatedUser() } returns UserDto.fromEntity(user)

        // When
        val result = inventoryListService.create(list)

        // Then
        assertEquals(list, result)
        verify { inventoryListRepository.save(list.toEntity()) }
    }

    @Test
    fun `update should save the updated list and return it`() {
        // Given
        val user = UserEntity(userName = "luke.skywalker")
        val id = 1L
        val list = InventoryList(id = id, name = "List 1", UserDto.fromEntity(user))
        val updatedList = list.copy(name = "Updated List")
        every { inventoryListRepository.findByIdOrNull(id) } returns list.toEntity()
        every { inventoryListRepository.save(updatedList.toEntity()) } returns updatedList.toEntity()
        every { userService.getAuthenticatedUser() } returns UserDto.fromEntity(user)

        // When
        val result = inventoryListService.update(id, updatedList)

        // Then
        assertEquals(updatedList, result)
    }

    @Test
    fun `delete should delete the list with the given id`() {
        // Given
        val user = UserEntity(userName = "luke.skywalker")
        val id = 1L
        val list = InventoryList(id = id, name = "List 1", UserDto.fromEntity(user))
        val localInventoryListRepository = mockk<InventoryListRepository>(relaxed = true)
        val localInventoryListService = InventoryListService(localInventoryListRepository, userService)

        every { localInventoryListRepository.deleteById(id) } returns Unit
        every { localInventoryListRepository.findByIdOrNull(id) } returns list.toEntity()
        every { userService.getAuthenticatedUser() } returns UserDto.fromEntity(user)
        // When
        localInventoryListService.delete(id)

        // Then
        verify { localInventoryListRepository.deleteById(id) }
    }
}