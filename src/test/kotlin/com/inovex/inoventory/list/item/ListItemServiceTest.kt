package com.inovex.inoventory.list.item

import com.inovex.inoventory.exceptions.ResourceNotFoundException
import com.inovex.inoventory.list.InventoryListRepository
import com.inovex.inoventory.list.entity.InventoryListEntity
import com.inovex.inoventory.user.entity.UserEntity
import com.inovex.inoventory.list.item.entity.ListItemEntity
import com.inovex.inoventory.list.item.dto.ListItem
import com.inovex.inoventory.product.ProductRepository
import com.inovex.inoventory.product.entity.ProductEntity
import com.inovex.inoventory.product.entity.SourceEntity
import com.inovex.inoventory.product.tag.entity.TagEntity
import io.mockk.mockk
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDate


class ListItemServiceTest {

    private val repository = mockk<ListItemRepository>()
    private val productRepository = mockk<ProductRepository>()
    private val listRepository = mockk<InventoryListRepository>()
    private val service = ListItemService(repository, productRepository, listRepository)

    @Test
    fun `getAll should return a list of ListItemDTOs`() {
        // Given
        val product =
            ProductEntity(name = "product", brands = "Some-Brand", ean = "1234567890", source = SourceEntity.USER)
        val product2 = ProductEntity(name = "product2", ean = "9876543210", source = SourceEntity.USER)

        val list = InventoryListEntity(id = 0L, name = "myList", user = UserEntity(userName = "luke.skywalker"))
        val listItem1 =
            ListItemEntity(id = 1L, expirationDate = LocalDate.of(2022, 1, 2), product = product, list = list)
        val listItem2 =
            ListItemEntity(id = 2L, expirationDate = LocalDate.of(2022, 1, 2), product = product, list = list)
        val listItem3 =
            ListItemEntity(id = 2L, expirationDate = LocalDate.of(2022, 1, 2), product = product2, list = list)

        every { repository.findAllByListId(0L) } returns listOf(listItem1, listItem2, listItem3)

        // When
        val result = service.getAll(0L)

        // Then
        assertEquals(2, result.size)

        val firstItemGroup = result[product.ean]!!
        val secondItemGroup = result[product2.ean]!!

        assertEquals(2, firstItemGroup.size)
        assertEquals(1, secondItemGroup.size)

        assertEquals(listItem1.product.ean, firstItemGroup[0].productEan)
        assertEquals(listItem1.list.id, firstItemGroup[0].listId)
        assertEquals(listItem1.expirationDate, firstItemGroup[0].expirationDate)
        var expectedDisplayName = "${listItem1.product.brands ?: ""} ${listItem1.product.name}".trim()
        assertEquals(expectedDisplayName, firstItemGroup[0].displayName)

        assertEquals(listItem2.product.ean, firstItemGroup[1].productEan)
        assertEquals(listItem2.list.id, firstItemGroup[1].listId)
        assertEquals(listItem2.expirationDate, firstItemGroup[1].expirationDate)
        expectedDisplayName = "${listItem2.product.brands ?: ""} ${listItem2.product.name}".trim()
        assertEquals(expectedDisplayName, firstItemGroup[1].displayName)

        assertEquals(listItem3.product.ean, secondItemGroup[0].productEan)
        assertEquals(listItem3.list.id, secondItemGroup[0].listId)
        assertEquals(listItem3.expirationDate, secondItemGroup[0].expirationDate)
        expectedDisplayName = "${listItem3.product.brands ?: ""} ${listItem3.product.name}".trim()
        assertEquals(expectedDisplayName, secondItemGroup[0].displayName)
    }

    @Test
    fun `getAllGroupBy should return a grouped map of ItemWrappers grouped by category`() {
        // Given
        val category1Name = "breakfast"
        val category2Name = "snacks"
        val product =
            ProductEntity(name = "breakfast product", brands = "Some-Brand", ean = "1234567890", source = SourceEntity.USER, tags = listOf(
                TagEntity(1, category1Name)
            ))
        val product2 = ProductEntity(name = "snack1", ean = "9876543210", source = SourceEntity.USER, tags = listOf(
            TagEntity(2, category2Name)
        ))
        val product3 = ProductEntity(name = "snack2", ean = "9876543299", source = SourceEntity.USER, tags = listOf(
            TagEntity(2, category2Name)
        ))


        val list = InventoryListEntity(id = 0L, name = "myList", user = UserEntity(userName = "luke.skywalker"))
        val listItem1 =
            ListItemEntity(id = 1L, expirationDate = LocalDate.of(2022, 1, 2), product = product, list = list)
        val listItem2 =
            ListItemEntity(id = 2L, expirationDate = LocalDate.of(2022, 1, 2), product = product, list = list)
        val listItem3 =
            ListItemEntity(id = 2L, expirationDate = LocalDate.of(2022, 1, 2), product = product2, list = list)
        val listItem4 =
            ListItemEntity(id = 2L, expirationDate = LocalDate.of(2022, 1, 2), product = product3, list = list)

        every { repository.findAllByListId(0L) } returns listOf(listItem1, listItem2, listItem3, listItem4)
        every { productRepository.findByEan(product.ean) } returns product
        every { productRepository.findByEan(product2.ean) } returns product2
        every { productRepository.findByEan(product3.ean) } returns product3

        // When
        val result = service.getAllGroupedBy(0L, "category")

        // Then
        assertEquals(2, result.size)

        val firstItemGroup = result[category1Name]!!
        val secondItemGroup = result[category2Name]!!

        assertEquals(1, firstItemGroup.size)
        assertEquals(2, secondItemGroup.size)

        assert(result.keys.contains(category1Name))
        assert(result.keys.contains(category2Name))

        assertEquals(2, firstItemGroup.first().items?.size)
        assertEquals(1, secondItemGroup[0].items?.size)
        assertEquals(1, secondItemGroup[1].items?.size)

    }

    @Test
    fun `findOrNull should return a ListItemDTO for the given id`() {
        // Given
        val id = 1L
        val product = ProductEntity(name = "product", ean = "1234567890", source = SourceEntity.USER)
        val list = InventoryListEntity(id = 0L, name = "myList", user = UserEntity(userName = "luke.skywalker"))
        val listItem =
            ListItemEntity(id = id, expirationDate = LocalDate.of(2022, 1, 1), product = product, list = list)
        every { repository.findByIdOrNull(id) } returns listItem

        // When
        val result = service.findOrNull(id, 0L)

        // Then
        assertEquals(id, result?.id)
        assertEquals(LocalDate.of(2022, 1, 1), result?.expirationDate)
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
        val product = ProductEntity(name = "product", ean = "1234567890", source = SourceEntity.USER)
        val list = InventoryListEntity(id = 0L, name = "myList", user = UserEntity(userName = "luke.skywalker"))
        val listItem = ListItem(productEan = "1234567890", expirationDate = LocalDate.of(2022, 1, 1), listId = 0)
        every { productRepository.findByEan(listItem.productEan) } returns product
        every { listRepository.findByIdOrNull(listItem.listId) } returns list
        every { repository.save(any()) } returns ListItemEntity(
            id = 1L,
            expirationDate = LocalDate.of(2022, 1, 1),
            product = product,
            list = list
        )

        // When
        val result = service.create(0L, listItem)

        // Then
        assertEquals(1L, result.id)
        assertEquals(LocalDate.of(2022, 1, 1), result.expirationDate)
        assertEquals(product.ean, result.productEan)
        assertEquals(list.id, result.listId)

    }

    @Test
    fun `create should throw a ResourceNotFoundException if the product is not found`() {
        // Given
        val listItem = ListItem(expirationDate = LocalDate.of(2022, 1, 1), productEan = "1234567890", listId = 0L)
        every { productRepository.findByEan(listItem.productEan) } returns null

        // When & Then
        assertThrows<ResourceNotFoundException> {
            service.create(0L, listItem)
        }
    }

    @Test
    fun `update should update an existing ListItem`() {
        // Given
        val id = 1L
        val product = ProductEntity(name = "product", ean = "1234567890", source = SourceEntity.USER)
        val list = InventoryListEntity(id = 0L, name = "myList", user = UserEntity(userName = "luke.skywalker"))
        val listItem = ListItem(productEan = "1234567890", expirationDate = LocalDate.of(2022,1,1), listId = 0L)
        val existingItem =
            ListItemEntity(id = id, expirationDate = LocalDate.of(2022, 1, 1), product = product, list = list)
        every { listRepository.findByIdOrNull(list.id) } returns list
        every { repository.findByIdOrNull(id) } returns existingItem
        every { repository.save(any()) } returns existingItem.copy(expirationDate = listItem.expirationDate)

        // When
        val result = service.update(id, 0L, listItem)

        // Then
        assertEquals(id, result.id)
        assertEquals(LocalDate.of(2022,1,1), result.expirationDate)
        assertEquals(product.ean, result.productEan)
        assertEquals(list.id, result.listId)
    }

    @Test
    fun `update should throw a ResourceNotFoundException if the ListItem is not found`() {
        // Given
        val id = 1L
        val listItem = ListItem(productEan = "1234567890", expirationDate = LocalDate.of(2022,1,1), listId = 0L)
        every { repository.findByIdOrNull(id) } returns null

        // When & Then
        assertThrows<ResourceNotFoundException> {
            service.update(id, 0L, listItem)
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