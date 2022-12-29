package com.inovex.inoventory.list.item

import com.inovex.inoventory.exceptions.ResourceNotFoundException
import com.inovex.inoventory.list.InventoryListRepository
import com.inovex.inoventory.list.domain.InventoryList
import com.inovex.inoventory.user.domain.User
import com.inovex.inoventory.list.item.domain.ListItem
import com.inovex.inoventory.list.item.dto.ListItemDTO
import com.inovex.inoventory.product.ProductRepository
import com.inovex.inoventory.product.domain.Product
import com.inovex.inoventory.product.domain.Source
import io.mockk.mockk
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.data.repository.findByIdOrNull


class ListItemServiceTest {

    private val repository = mockk<ListItemRepository>()
    private val productRepository = mockk<ProductRepository>()
    private val listRepository = mockk<InventoryListRepository>()
    private val service = ListItemService(repository, productRepository, listRepository)

    @Test
    fun `getAll should return a list of ListItemDTOs`() {
        // Given
        val product = Product(name = "product", ean = "1234567890", source = Source.USER)
        val list = InventoryList(id = 0L, name = "myList", user = User(userName = "luke.skywalker"))
        val listItem1 = ListItem(id = 1L, expirationDate = "2022-01-01", product = product, list = list)
        val listItem2 = ListItem(id = 2L, expirationDate = "2022-01-02", product = product, list = list)
        every { repository.findAllByListId(0L) } returns listOf(listItem1, listItem2)

        // When
        val result = service.getAll(0L)

        // Then
        assertEquals(2, result.size)
        assertEquals(1L, result[0].id)
        assertEquals("2022-01-01", result[0].expirationDate)
        assertEquals(product.ean, result[0].productEan)
        assertEquals(2L, result[1].id)
        assertEquals("2022-01-02", result[1].expirationDate)
        assertEquals(product.ean, result[1].productEan)
    }

    @Test
    fun `findOrNull should return a ListItemDTO for the given id`() {
        // Given
        val id = 1L
        val product = Product(name = "product", ean = "1234567890", source = Source.USER)
        val list = InventoryList(id = 0L, name = "myList", user = User(userName = "luke.skywalker"))
        val listItem = ListItem(id = id, expirationDate = "2022-01-01", product = product, list = list)
        every { repository.findByIdOrNull(id) } returns listItem

        // When
        val result = service.findOrNull(id, 0L)

        // Then
        assertEquals(id, result?.id)
        assertEquals("2022-01-01", result?.expirationDate)
        assertEquals(product.ean, result?.productEan)
    }

    @Test
    fun `findOrNull should return null if no ListItem is found`() {
        // Given
        val id = 1L
        every { repository.findByIdOrNull(id) } returns null

        // When
        val result = service.findOrNull(id, 0L)

        // Then
        assertNull(result)
    }

    @Test
    fun `create should create a new ListItem`() {
        // Given
        val product = Product(name = "product", ean = "1234567890", source = Source.USER)
        val list = InventoryList(id = 0L, name = "myList", user = User(userName = "luke.skywalker"))
        val listItemDto = ListItemDTO(productEan = "1234567890", expirationDate = "2022-01-01", listId = 0)
        every { productRepository.findByEan(listItemDto.productEan) } returns product
        every { listRepository.findByIdOrNull(listItemDto.listId) } returns list
        every { repository.save(any()) } returns ListItem(id = 1L, expirationDate = "2022-01-01", product = product, list = list)

        // When
        val result = service.create(0L, listItemDto)

        // Then
        assertEquals(1L, result.id)
        assertEquals("2022-01-01", result.expirationDate)
        assertEquals(product.ean, result.productEan)
        assertEquals(list.id, result.listId)

    }
    @Test
    fun `create should throw a ResourceNotFoundException if the product is not found`() {
        // Given
        val listItemDto = ListItemDTO(expirationDate = "2022-01-01", productEan = "1234567890", listId = 0L)
        every { productRepository.findByEan(listItemDto.productEan) } returns null

        // When & Then
        assertThrows<ResourceNotFoundException> {
            service.create(0L, listItemDto)
        }
    }

    @Test
    fun `update should update an existing ListItem`() {
        // Given
        val id = 1L
        val product = Product(name = "product", ean = "1234567890", source = Source.USER)
        val list = InventoryList(id = 0L, name = "myList", user = User(userName = "luke.skywalker"))
        val listItemDto = ListItemDTO(productEan = "1234567890", expirationDate = "2022-01-02", listId = 0L)
        val existingItem = ListItem(id = id, expirationDate = "2022-01-01", product = product, list = list)
        every { listRepository.findByIdOrNull(list.id) } returns list
        every { repository.findByIdOrNull(id) } returns existingItem
        every { repository.save(any()) } returns existingItem.copy(expirationDate = listItemDto.expirationDate)

        // When
        val result = service.update(id, 0L, listItemDto)

        // Then
        assertEquals(id, result.id)
        assertEquals("2022-01-02", result.expirationDate)
        assertEquals(product.ean, result.productEan)
        assertEquals(list.id, result.listId)
    }

    @Test
    fun `update should throw a ResourceNotFoundException if the ListItem is not found`() {
        // Given
        val id = 1L
        val listItemDto = ListItemDTO(productEan = "1234567890", expirationDate = "2022-01-02", listId = 0L)
        every { repository.findByIdOrNull(id) } returns null

        // When & Then
        assertThrows<ResourceNotFoundException> {
            service.update(id, 0L, listItemDto)
        }
    }

    @Test
    fun `delete should delete the ListItem with the given id`() {
        // Given
        val id = 1L
        every { repository.deleteById(id) } returns Unit

        // When
        service.delete(id)

        // Then
        verify { repository.deleteById(id) }
    }

}