import com.inovex.inoventory.InoventoryApplication
import com.inovex.inoventory.list.domain.InventoryList
import com.inovex.inoventory.list.service.InventoryListService
import com.inovex.inoventory.user.UserRepository
import com.inovex.inoventory.user.domain.User
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles


@ActiveProfiles("test")
@SpringBootTest(
    classes = [InoventoryApplication::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class InventoryListControllerIntegrationTests {

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Autowired
    lateinit var inventoryListService: InventoryListService

    @Autowired
    lateinit var userRepository: UserRepository

    @AfterEach
    fun cleanUsersTable() {
        inventoryListService.getAll().forEach { inventoryListService.delete(it.id!!) }
        userRepository.deleteAll()
    }

    @Test
    fun `GET - getAll should return all inventory lists`() {
        // Given
        val user = userRepository.save(User(userName = "luke.skywalker"))
        val expectedInventoryLists = listOf(
            InventoryList(name = "Grocery List", user = user),
            InventoryList(name = "Household Items", user = user)
        )
        expectedInventoryLists.forEach { inventoryListService.create(it) }

        // When
        val response = restTemplate.getForEntity("/api/v1/inventory-lists", Array<InventoryList>::class.java)
        print(response.body)

        // Then
        assertNotNull(response)
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(2, response.body?.size)
    }

    @Test
    fun `GET {ID} - getting list by id should return the list with the given id`() {
        // Given
        val user = userRepository.save(User(userName = "luke.skywalker"))
        val expectedInventoryLists = listOf(
            InventoryList(name = "Grocery List", user = user),
            InventoryList(name = "Household Items", user = user)
        )

        expectedInventoryLists.forEach {
            // When
            val expected = inventoryListService.create(it)
            val response =
                restTemplate.getForEntity("/api/v1/inventory-lists/${expected.id}", InventoryList::class.java)

            // Then
            assertNotNull(response)
            assertEquals(HttpStatus.OK, response.statusCode)
            assertEquals(expected.id, response.body?.id)
            assertEquals(expected.name, response.body?.name)
        }
    }

    @Test
    fun `GET {ID} 404 - getting list by non existing id should return status 404`() {
        // Given
        val user = userRepository.save(User(userName = "luke.skywalker"))
        val list = InventoryList(id = 1L, name = "Grocery List", user = user)


        // When
        val expected = inventoryListService.create(list)
        val response =
            restTemplate.getForEntity("/api/v1/inventory-lists/${expected.id!!+42}", String::class.java)

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun `POST - create a new list works and returns created list`() {
        // Given
        val user = userRepository.save(User(userName = "luke.skywalker"))
        val toCreate = InventoryList(id = 1L, name = "Grocery List", user = user)

        // When
        val response =
            restTemplate.postForEntity("/api/v1/inventory-lists", toCreate, InventoryList::class.java)

        // Then
        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals(toCreate.name, response.body?.name)
    }

    @Test
    fun `DELETE - delete a list`() {
        // Given
        val user = userRepository.save(User(userName = "luke.skywalker"))
        assertTrue(inventoryListService.getAll().isEmpty())
        val existingList = inventoryListService.create(InventoryList(id = 1L, name = "Grocery List", user = user))
        assertTrue(inventoryListService.getAll().isNotEmpty())

        // When
        restTemplate.delete("/api/v1/inventory-lists/${existingList.id}")

        // Then
        assertTrue(inventoryListService.getAll().isEmpty())
    }

//    @Test
//    fun `PUT - updates an existing list`() {
//        // Given
//        val user = userRepository.save(User(userName = "luke.skywalker"))
//        val existingList = inventoryListService.create(InventoryList(id = 1L, name = "Grocery List", user = user))
//
//
//        // When
//        val updatedList = existingList.copy(name = "newListName")
//        val response =
//            restTemplate.put("/api/v1/inventory-lists/${existingList.id}", updatedList, InventoryList::class.java)
//
//        // Then
//        assertEquals(HttpStatus.CREATED, response.statusCode)
//        assertEquals(updatedList.name, response.body?.name)
//    }


}