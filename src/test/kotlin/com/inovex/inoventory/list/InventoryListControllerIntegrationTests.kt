import com.c4_soft.springaddons.security.oauth2.test.annotations.OpenIdClaims
import com.c4_soft.springaddons.security.oauth2.test.annotations.WithMockJwtAuth
import com.fasterxml.jackson.databind.ObjectMapper
import com.inovex.inoventory.InoventoryApplication
import com.inovex.inoventory.list.domain.InventoryList
import com.inovex.inoventory.list.service.InventoryListService
import com.inovex.inoventory.user.UserRepository
import com.inovex.inoventory.user.domain.User
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.util.*


@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(
    classes = [InoventoryApplication::class],
)
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
    @WithMockJwtAuth(
        authorities = ["inoventory_user"],
        tokenString = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJONy1wdi1FM296UDlFWTdudVZiUDBlUHZWaGlmN3YyTW9xR2pZbXFCM3p3In0.eyJleHAiOjE2NzI2ODczMzYsImlhdCI6MTY3MjY4NzAzNiwianRpIjoiODE0ZDQ1YWQtNmI3OC00MjEyLWI0OTMtOTI2MWQwNDkwMWZhIiwiaXNzIjoiaHR0cDovLzEwLjEwMC4yNTUuNzY6ODA4MS9yZWFsbXMvaW5vdmVudG9yeSIsImF1ZCI6ImFjY291bnQiLCJzdWIiOiI3NWFlZTBiZC1jMGVlLTQ3YjEtYjcyNy1iNTg4MmQ0ZTE2OWMiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJhcHAiLCJzZXNzaW9uX3N0YXRlIjoiNDRjMDA5ZDEtNTZjYi00ZTQwLWFhNTctZDI5Y2ZlZjFlZjM3IiwiYWNyIjoiMSIsInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJvZmZsaW5lX2FjY2VzcyIsImRlZmF1bHQtcm9sZXMtaW5vdmVudG9yeSIsInVtYV9hdXRob3JpemF0aW9uIiwiaW5vdmVudG9yeS11c2VyIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJwcm9maWxlIGVtYWlsIiwic2lkIjoiNDRjMDA5ZDEtNTZjYi00ZTQwLWFhNTctZDI5Y2ZlZjFlZjM3IiwiZW1haWxfdmVyaWZpZWQiOnRydWUsIm5hbWUiOiJUZXN0IFVzZXIiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJ0ZXN0LWFwcC11c2VyIiwiZ2l2ZW5fbmFtZSI6IlRlc3QiLCJmYW1pbHlfbmFtZSI6IlVzZXIiLCJlbWFpbCI6InRlc3RAdGVzdC5jb20ifQ.SNMezJsmz-Tvde78J4VHUwU9eFzC1d1VUpPa-uJ2ZGitdyJaBaBr_Dt3_Wfirono2TmOxfEV78XxEVq50LrxW8qsz5oNUXU52ePpBjJoxmwN3F8_Vej4wkcf_VoBF0C7JSytXRNnpIATB4rWMCRq-zsfl0fbhkpbwTlsHYBdHSSn8A5QMtSexstN-rylMoGy-hXQuim28Q7BPmf9olaDgOPlfPU0S8A7Z9p4uLlmQp70boJJWzrtixBuLy134p2MoOgq4FauVUpvSzxJaMP7ncw7ZfywM_BsW9ASfyVqiTFoHOtP7oH3JRDrwv5mEmBhWSL_Bp7-i80dvntkdIXhbw",
        claims = OpenIdClaims(
//            sub = USER_ID,
//            preferredUsername = "Integration Test User",
            sub = USER_ID,
            phoneNumberVerified = false,
            preferredUsername = "user"
        )
    )
    fun `GET - getAll should return all inventory lists`() {
        // Given
        val user = userRepository.save(TEST_USER)
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
    @WithMockJwtAuth(
        authorities = ["inoventory_user"],
        claims = OpenIdClaims(
            sub = USER_ID,
            preferredUsername = "Integration Test User"
        )
    )
    fun `GET {ID} - getting list by id should return the list with the given id`() {
        // Given
        val user = userRepository.save(TEST_USER)
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
    @WithMockJwtAuth(
        authorities = ["inoventory_user"],
        claims = OpenIdClaims(
            sub = USER_ID,
            preferredUsername = "Integration Test User"
        )
    )
    fun `GET {ID} 404 - getting list by non existing id should return status 404`() {
        // Given
        val user = userRepository.save(TEST_USER)
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
    @WithMockJwtAuth(
        authorities = ["inoventory_user"],
        claims = OpenIdClaims(
            sub = USER_ID,
            preferredUsername = "Integration Test User"
        )
    )
    fun `POST - create a new list works and returns created list`() {
        // Given
        val user = userRepository.save(TEST_USER)
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
    @WithMockJwtAuth(
        authorities = ["inoventory_user"],
        claims = OpenIdClaims(
            sub = USER_ID,
            preferredUsername = "Integration Test User"
        )
    )
    fun `DELETE - delete a list`() {
        // Given
        val user = userRepository.save(TEST_USER)
        assertTrue(inventoryListService.getAll().isEmpty())
        val existingList = inventoryListService.create(InventoryList(id = 1L, name = "Grocery List", user = user))
        assertTrue(inventoryListService.getAll().isNotEmpty())

        // When
        mockMvc.delete("/api/v1/inventory-lists/${existingList.id}")
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/v1/inventory-lists/${existingList.id}")
        ).andExpect(MockMvcResultMatchers.status().isOk)

        // Then
        assertTrue(inventoryListService.getAll().isEmpty())
    }

    @Test
    @WithMockJwtAuth(
        authorities = ["inoventory_user"],
        claims = OpenIdClaims(
            sub = USER_ID,
            preferredUsername = "Integration Test User"
        )
    )
    fun `PUT - updates an existing list`() {
        // Given
        val user = userRepository.save(TEST_USER)
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

    companion object {
        const val USER_ID = "328d58cc-1973-4e53-99d4-f49e8b48dc60"
        val TEST_USER = User(id = UUID.fromString(USER_ID), userName = "luke.skywalker")
    }

}