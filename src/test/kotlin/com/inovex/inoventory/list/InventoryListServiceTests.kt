import com.inovex.inoventory.exceptions.NotAuthorizedException
import com.inovex.inoventory.exceptions.ResourceNotFoundException
import com.inovex.inoventory.list.InventoryListRepository
import com.inovex.inoventory.list.service.InventoryListServiceImpl
import com.inovex.inoventory.list.domain.InventoryList
import com.inovex.inoventory.user.domain.User
import com.inovex.inoventory.user.service.UserService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.Optional

class InventoryListServiceTests {

    private val inventoryListRepository = mockk<InventoryListRepository>()
    private val userService = mockk<UserService>()
    private val inventoryListService = InventoryListServiceImpl(inventoryListRepository, userService )

    @Test
    fun `getAll should return all lists from the repository`() {
        // Given
        val user = User(0, userName = "luke.skywalker")
        val list1 = InventoryList(name = "List 1", user = user)
        val list2 = InventoryList(name = "List 2", user = user)
        val lists = listOf(list1, list2)
        every { inventoryListRepository.findAll() } returns lists
        every { userService.getAuthenticatedUser() } returns user

        // When
        val result = inventoryListService.getAll()

        // Then
        assertEquals(lists, result)
        verify { inventoryListRepository.findAll() }
    }

    @Test
    fun `getById should return the list with the given id`() {
        // Given
        val user = User(0, userName = "luke.skywalker")
        val id = 1L
        val list = InventoryList(id = id, name = "List 1", user)
        every { inventoryListRepository.findById(id) } returns Optional.of(list)
        every { userService.getAuthenticatedUser() } returns user

        // When
        val result = inventoryListService.getById(id)

        // Then
        assertEquals(list, result)
        verify { inventoryListRepository.findById(id) }
    }

    @Test
    fun `getById should throw ResourceNotFoundException when list is not found`() {
        // Given
        val user = User(0, userName = "luke.skywalker")
        val id = 1L
        every { inventoryListRepository.findById(id) } returns Optional.empty()
        every { userService.getAuthenticatedUser() } returns user

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
        val user = User(0, userName = "luke.skywalker")
        val list = InventoryList(name = "List 1", user = user)
        every { inventoryListRepository.save(list) } returns list
        every { userService.getAuthenticatedUser() } returns user

        // When
        val result = inventoryListService.create(list)

        // Then
        assertEquals(list, result)
        verify { inventoryListRepository.save(list) }
    }

    @Test
    fun `create should throw NotAuthorizedException when no user is authenticated`() {
        // Given
        val user = User(0, userName = "luke.skywalker")
        val list = InventoryList(name = "List 1", user = user)
        every { inventoryListRepository.save(list) } returns list
        every { userService.getAuthenticatedUser() } returns null

        // When
        val exception = assertThrows<NotAuthorizedException> {
            inventoryListService.create(list)
        }

        // Then
        assertEquals("You must be logged in to perform this action", exception.message)
        verify { userService.getAuthenticatedUser() }
    }

    @Test
    fun `update should save the updated list and return it`() {
        // Given
        val user = User(0, userName = "luke.skywalker")
        val id = 1L
        val list = InventoryList(id = id, name = "List 1", user)
        val updatedList = list.copy(name = "Updated List")
        every { inventoryListRepository.findById(id) } returns Optional.of(list)
        every { inventoryListRepository.save(updatedList) } returns updatedList

        // When
        val result = inventoryListService.update(id, updatedList)

        // Then
        assertEquals(updatedList, result)
        verify { inventoryListRepository.findById(id) }
        verify { inventoryListRepository.save(updatedList) }
    }

//    @Test
//    fun `delete should delete the list with the given id`() {
//        // Given
//        val id = 1L
//        every { inventoryListRepository.deleteById(id) }
//
//        // When
//        inventoryListService.delete(id)
//
//        // Then
//        verify { inventoryListRepository.deleteById(id) }
//    }
}