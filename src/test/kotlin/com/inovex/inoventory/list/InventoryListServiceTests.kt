import com.inovex.inoventory.exceptions.ResourceNotFoundException
import com.inovex.inoventory.list.InventoryListRepository
import com.inovex.inoventory.list.domain.InventoryList
import com.inovex.inoventory.list.dto.InventoryListDto
import com.inovex.inoventory.list.service.InventoryListService
import com.inovex.inoventory.user.domain.User
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
        val user = User(userName = "luke.skywalker")
        val list1 = InventoryList(name = "List 1", user = user)
        val list2 = InventoryList(name = "List 2", user = user)
        val lists = listOf(list1, list2)
        every { inventoryListRepository.findByUserId(user.id) } returns lists
        every { userService.getAuthenticatedUser() } returns UserDto.fromDomain(user)

        // When
        val result = inventoryListService.getAll()

        // Then
        assertEquals(lists, result)
    }

    @Test
    fun `getById should return the list with the given id`() {
        // Given
        val user = User(userName = "luke.skywalker")
        val id = 1L
        val list = InventoryListDto(id = id, name = "List 1", UserDto.fromDomain(user))
        every { inventoryListRepository.findByIdOrNull(id) } returns list.toDomain()
        every { userService.getAuthenticatedUser() } returns UserDto.fromDomain(user)

        // When
        val result = inventoryListService.getById(id)

        // Then
        assertEquals(list, result)
        verify { inventoryListRepository.findById(id) }
    }

    @Test
    fun `getById should throw ResourceNotFoundException when list is not found`() {
        // Given
        val user = User(userName = "luke.skywalker")
        val id = 1L
        every { inventoryListRepository.findById(id) } returns Optional.empty()
        every { userService.getAuthenticatedUser() } returns UserDto.fromDomain(user)

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
        val user = User(userName = "luke.skywalker")
        val list = InventoryListDto(name = "List 1", user = UserDto.fromDomain(user))
        every { inventoryListRepository.save(list.toDomain()) } returns list.toDomain()
        every { userService.getAuthenticatedUser() } returns UserDto.fromDomain(user)

        // When
        val result = inventoryListService.create(list)

        // Then
        assertEquals(list, result)
        verify { inventoryListRepository.save(list.toDomain()) }
    }

    @Test
    fun `update should save the updated list and return it`() {
        // Given
        val user = User(userName = "luke.skywalker")
        val id = 1L
        val list = InventoryListDto(id = id, name = "List 1", UserDto.fromDomain(user))
        val updatedList = list.copy(name = "Updated List")
        every { inventoryListRepository.findByIdOrNull(id) } returns list.toDomain()
        every { inventoryListRepository.save(updatedList.toDomain()) } returns updatedList.toDomain()
        every { userService.getAuthenticatedUser() } returns UserDto.fromDomain(user)

        // When
        val result = inventoryListService.update(id, updatedList)

        // Then
        assertEquals(updatedList, result)
    }

    @Test
    fun `delete should delete the list with the given id`() {
        // Given
        val user = User(userName = "luke.skywalker")
        val id = 1L
        val list = InventoryListDto(id = id, name = "List 1", UserDto.fromDomain(user))
        val localInventoryListRepository = mockk<InventoryListRepository>(relaxed = true)
        val localInventoryListService = InventoryListService(localInventoryListRepository, userService)

        every { localInventoryListRepository.deleteById(id) } returns Unit
        every { localInventoryListRepository.findByIdOrNull(id) } returns list.toDomain()
        every { userService.getAuthenticatedUser() } returns UserDto.fromDomain(user)
        // When
        localInventoryListService.delete(id)

        // Then
        verify { localInventoryListRepository.deleteById(id) }
    }
}