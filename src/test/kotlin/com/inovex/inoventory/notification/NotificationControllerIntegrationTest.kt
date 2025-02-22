package com.inovex.inoventory.notification

import com.fasterxml.jackson.databind.ObjectMapper
import com.inovex.inoventory.list.InventoryListService
import com.inovex.inoventory.list.dto.InventoryList
import com.inovex.inoventory.list.item.ListItemRepository
import com.inovex.inoventory.list.item.ListItemService
import com.inovex.inoventory.list.item.dto.ListItem
import com.inovex.inoventory.list.item.entity.ListItemEntity
import com.inovex.inoventory.mock.TestConfig
import com.inovex.inoventory.notification.dto.Notification
import com.inovex.inoventory.product.ProductRepository
import com.inovex.inoventory.product.ProductService
import com.inovex.inoventory.product.dto.EAN
import com.inovex.inoventory.product.dto.Product
import com.inovex.inoventory.product.entity.SourceEntity
import com.inovex.inoventory.product.tag.dto.Tag
import com.inovex.inoventory.user.UserRepository
import com.inovex.inoventory.user.dto.UserDto
import com.inovex.inoventory.user.entity.UserEntity
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Profile
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import spock.lang.Specification
import java.time.Instant
import java.time.LocalDate

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = [TestConfig::class])
internal class NotificationControllerIntegrationTest : Specification() {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var inventoryListService: InventoryListService

    @Autowired
    lateinit var productService: ProductService

    @Autowired
    lateinit var productRepository: ProductRepository

    @Autowired
    lateinit var listItemService: ListItemService

    @Autowired
    lateinit var listItemRepository: ListItemRepository

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var notificationRepository: NotificationRepository


    @BeforeEach
    fun cleanUsersTable() {
        notificationRepository.deleteAll()
        listItemRepository.deleteAll()
        inventoryListService.getAll().forEach { inventoryListService.delete(it.id!!) }
        productRepository.deleteAll()
        userRepository.deleteAll()
    }

    @Test
    @WithMockUser
    fun `GET notifications works`() {
        val user = UserDto.fromEntity(userRepository.save(UserEntity(userName = "luke.skywalker")))
        var list = InventoryList(name = "Grocery List", user = user)
        list = inventoryListService.create(list)

        val products = listOf(
            Product(name = "MyProduct1", ean = EAN("01234567"), tags = listOf(Tag("tag"))),
            Product(name = "MyProduct2", ean = EAN("12345678"), tags = listOf(Tag("tag")))
        )
        products.forEach { productRepository.save(it.toEntity(SourceEntity.USER, Instant.now())) }

        val res = productService.findAll()
        print(res)

        val date1 = LocalDate.of(2022, 1, 2)
        val date2 = LocalDate.of(2023, 1, 2)
        val items = listOf(
            ListItem(productEan = products[0].ean.toString(), expirationDate = date1, listId = list.id!!),
            ListItem(productEan = products[1].ean.toString(), expirationDate = date1, listId = list.id!!),
            ListItem(productEan = products[1].ean.toString(), expirationDate = date2, listId = list.id!!),
        )
        items.forEach { listItemService.create(list.id!!, it) }

        val expected = listOf(
            Notification("20220102${list.id!!}".toInt(), count = 2, date = date1),
            Notification("20230102${list.id!!}".toInt(), count = 1, date = date2),
        )

        val result = mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/inoventory-lists/${list.id!!}/notifications")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk).andReturn()

        val actual = objectMapper.readValue(result.response.contentAsString, Array<Notification>::class.java).toList()

        Assertions.assertEquals(expected, actual)
    }
}