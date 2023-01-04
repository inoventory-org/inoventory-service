import com.c4_soft.springaddons.security.oauth2.test.annotations.WithMockJwtAuth
import com.fasterxml.jackson.databind.ObjectMapper
import com.inovex.inoventory.InoventoryApplication
import com.inovex.inoventory.list.dto.InventoryList
import com.inovex.inoventory.list.InventoryListService
import com.inovex.inoventory.mock.TestConfig
import com.inovex.inoventory.user.UserRepository
import com.inovex.inoventory.user.entity.UserEntity
import com.inovex.inoventory.user.dto.UserDto
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers


@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(
    classes = [InoventoryApplication::class],
)
@ContextConfiguration(classes = [TestConfig::class])
class InventoryListControllerIntegrationTests {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

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
    @WithMockJwtAuth(authorities = ["inoventory-user"])
    fun `GET - getAll should return all inventory lists`() {
        // Given
        val user = UserDto.fromEntity(userRepository.save(UserEntity(userName = "luke.skywalker")))
        val expectedInventoryLists = listOf(
            InventoryList(name = "Grocery List", user = user),
            InventoryList(name = "Household Items", user = user)
        )
        expectedInventoryLists.forEach { inventoryListService.create(it) }

        // When
        val result = mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/inventory-lists").accept(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isOk).andReturn()
        val actual = objectMapper.readValue(result.response.contentAsString, Array<InventoryList>::class.java)

        // Then
        assertEquals(2, actual.size)
    }

    @Test
    @WithMockJwtAuth(authorities = ["inoventory-user"])
    fun `GET {ID} - getting list by id should return the list with the given id`() {
        // Given
        val user = UserDto.fromEntity(userRepository.save(UserEntity(userName = "luke.skywalker")))
        val expectedInventoryLists = listOf(
            InventoryList(name = "Grocery List", user = user),
            InventoryList(name = "Household Items", user = user)
        )

        expectedInventoryLists.forEach {
            // When
            val expected = inventoryListService.create(it)
            val result = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/inventory-lists/${expected.id}")
            ).andExpect(MockMvcResultMatchers.status().isOk).andReturn()
            val actual = objectMapper.readValue(result.response.contentAsString, InventoryList::class.java)

            // Then
            assertEquals(expected.id, actual.id)
            assertEquals(expected.name, actual.name)
        }
    }

    @Test
    @WithMockJwtAuth(authorities = ["inoventory-user"])
    fun `GET {ID} 404 - getting list by non existing id should return status 404`() {
        // Given
        val user = UserDto.fromEntity(userRepository.save(UserEntity(userName = "luke.skywalker")))
        val list = InventoryList(id = 1L, name = "Grocery List", user = user)


        // When
        val expected = inventoryListService.create(list)
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/inventory-lists/${expected.id!! + 42}")
        )

            // Then
            .andExpect(MockMvcResultMatchers.status().isNotFound)
    }

    @Test
    @WithMockJwtAuth(authorities = ["inoventory-user"])
    fun `POST - create a new list works and returns created list`() {
        // Given
        val user = UserDto.fromEntity(userRepository.save(UserEntity(userName = "luke.skywalker")))
        val toCreate = InventoryList(id = 1L, name = "Grocery List", user = user)

        // When
        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/inventory-lists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(toCreate))
        ).andExpect(MockMvcResultMatchers.status().isCreated).andReturn()
        val actual = objectMapper.readValue(result.response.contentAsString, InventoryList::class.java)

        // Then
        assertEquals(toCreate.name, actual.name)
    }

    @Test
    @WithMockJwtAuth(authorities = ["inoventory-user"])
    fun `DELETE - delete a list`() {
        // Given
        val user = UserDto.fromEntity(userRepository.save(UserEntity(userName = "luke.skywalker")))
        assertTrue(inventoryListService.getAll().isEmpty())
        val existingList = inventoryListService.create(InventoryList(id = 1L, name = "Grocery List", user = user))
        assertTrue(inventoryListService.getAll().isNotEmpty())

        // When
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/v1/inventory-lists/${existingList.id}")
        ).andExpect(MockMvcResultMatchers.status().isOk)

        // Then
        assertTrue(inventoryListService.getAll().isEmpty())
    }

    @Test
    @WithMockJwtAuth(authorities = ["inoventory-user"])
    fun `PUT - updates an existing list`() {
        // Given
        val user = UserDto.fromEntity(userRepository.save(UserEntity(userName = "luke.skywalker")))
        val existingList = inventoryListService.create(InventoryList(id = 1L, name = "Grocery List", user = user))


        // When
        val updatedList = existingList.copy(name = "newListName")
        val result = mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/inventory-lists/${existingList.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedList))
        ).andExpect(MockMvcResultMatchers.status().isOk).andReturn()
        val actual = objectMapper.readValue(result.response.contentAsString, InventoryList::class.java)


        // Then
        assertEquals(updatedList.name, actual.name)

        val listToCheck = inventoryListService.getById(existingList.id!!)
        assertEquals(updatedList.name, listToCheck.name)
    }
}