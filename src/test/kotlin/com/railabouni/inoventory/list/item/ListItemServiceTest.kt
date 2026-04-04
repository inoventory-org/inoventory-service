package com.railabouni.inoventory.list.item

import com.railabouni.inoventory.exceptions.ResourceNotFoundException
import com.railabouni.inoventory.list.InventoryListRepository
import com.railabouni.inoventory.list.entity.InventoryListEntity
import com.railabouni.inoventory.list.item.dto.ListItem
import com.railabouni.inoventory.list.item.entity.ListItemEntity
import com.railabouni.inoventory.config.DbAuthContext
import com.railabouni.inoventory.openfoodfacts.api.EanApiConnector
import com.railabouni.inoventory.product.ProductCacheProperties
import com.railabouni.inoventory.product.ProductMemoryCache
import com.railabouni.inoventory.product.ProductService
import com.railabouni.inoventory.product.dto.EAN
import com.railabouni.inoventory.product.dto.Product
import com.railabouni.inoventory.product.tag.dto.Tag
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDate
import java.util.UUID

class ListItemServiceTest {

    private val repository = mockk<ListItemRepository>()
    private val apiConnector = mockk<EanApiConnector>()
    private val productService = ProductService(apiConnector, ProductMemoryCache(ProductCacheProperties()))
    private val listRepository = mockk<InventoryListRepository>()
    private val dbAuthContext = mockk<DbAuthContext>(relaxed = true)
    private val service = ListItemService(repository, productService, listRepository, dbAuthContext)

    @Test
    fun `getAll should return a list of ListItemDTOs`() {
        val list = InventoryListEntity(id = 0L, name = "myList", userId = UUID.randomUUID())
        val listItem1 = ListItemEntity(
            id = 1L,
            expirationDate = LocalDate.of(2022, 1, 2),
            productEan = "1234567890123",
            productName = "product",
            productBrands = "Some-Brand",
            list = list
        )
        val listItem2 = ListItemEntity(
            id = 2L,
            expirationDate = LocalDate.of(2022, 1, 2),
            productEan = "1234567890123",
            productName = "product",
            productBrands = "Some-Brand",
            list = list
        )
        val listItem3 = ListItemEntity(
            id = 3L,
            expirationDate = LocalDate.of(2022, 1, 2),
            productEan = "9876543210123",
            productName = "product2",
            list = list
        )

        every { repository.findAllByListId(0L) } returns listOf(listItem1, listItem2, listItem3)

        val result = service.getAll(0L)

        assertEquals(2, result.size)

        val firstItemGroup = result[listItem1.productEan]!!
        val secondItemGroup = result[listItem3.productEan]!!

        assertEquals(2, firstItemGroup.size)
        assertEquals(1, secondItemGroup.size)

        assertEquals(listItem1.productEan, firstItemGroup[0].productEan)
        assertEquals(listItem1.list.id, firstItemGroup[0].listId)
        assertEquals(listItem1.expirationDate, firstItemGroup[0].expirationDate)
        var expectedDisplayName = "${listItem1.productBrands ?: ""} ${listItem1.productName}".trim()
        assertEquals(expectedDisplayName, firstItemGroup[0].displayName)

        assertEquals(listItem2.productEan, firstItemGroup[1].productEan)
        assertEquals(listItem2.list.id, firstItemGroup[1].listId)
        assertEquals(listItem2.expirationDate, firstItemGroup[1].expirationDate)
        expectedDisplayName = "${listItem2.productBrands ?: ""} ${listItem2.productName}".trim()
        assertEquals(expectedDisplayName, firstItemGroup[1].displayName)

        assertEquals(listItem3.productEan, secondItemGroup[0].productEan)
        assertEquals(listItem3.list.id, secondItemGroup[0].listId)
        assertEquals(listItem3.expirationDate, secondItemGroup[0].expirationDate)
        expectedDisplayName = "${listItem3.productBrands ?: ""} ${listItem3.productName}".trim()
        assertEquals(expectedDisplayName, secondItemGroup[0].displayName)
    }

    @Test
    fun `getAllGroupBy should return a grouped map of ItemWrappers grouped by category`() {
        val category1Name = "breakfast"
        val category2Name = "snacks"
        val product1 = Product(
            ean = EAN("1234567890123"),
            name = "breakfast product",
            brands = "Some-Brand",
            tags = listOf(Tag(category1Name))
        )
        val product2 = Product(
            ean = EAN("9876543210123"),
            name = "snack1",
            tags = listOf(Tag(category2Name))
        )
        val product3 = Product(
            ean = EAN("9876543299123"),
            name = "snack2",
            tags = listOf(Tag(category2Name))
        )

        val list = InventoryListEntity(id = 0L, name = "myList", userId = UUID.randomUUID())
        val listItem1 = ListItemEntity(
            id = 1L,
            expirationDate = LocalDate.of(2022, 1, 2),
            productEan = product1.ean.value,
            productName = product1.name,
            productBrands = product1.brands,
            list = list
        )
        val listItem2 = ListItemEntity(
            id = 2L,
            expirationDate = LocalDate.of(2022, 1, 2),
            productEan = product1.ean.value,
            productName = product1.name,
            productBrands = product1.brands,
            list = list
        )
        val listItem3 = ListItemEntity(
            id = 3L,
            expirationDate = LocalDate.of(2022, 1, 2),
            productEan = product2.ean.value,
            productName = product2.name,
            list = list
        )
        val listItem4 = ListItemEntity(
            id = 4L,
            expirationDate = LocalDate.of(2022, 1, 2),
            productEan = product3.ean.value,
            productName = product3.name,
            list = list
        )

        every { repository.findAllByListId(0L) } returns listOf(listItem1, listItem2, listItem3, listItem4)
        coEvery { apiConnector.findByEan(EAN(product1.ean.value)) } returns product1
        coEvery { apiConnector.findByEan(EAN(product2.ean.value)) } returns product2
        coEvery { apiConnector.findByEan(EAN(product3.ean.value)) } returns product3

        val result = service.getAllGroupedBy(0L, "category")

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
        val id = 1L
        val list = InventoryListEntity(id = 0L, name = "myList", userId = UUID.randomUUID())
        val listItem = ListItemEntity(
            id = id,
            expirationDate = LocalDate.of(2022, 1, 1),
            productEan = "1234567890123",
            productName = "product",
            list = list
        )
        every { repository.findByIdOrNull(id) } returns listItem

        val result = service.findOrNull(id, 0L)

        assertEquals(id, result?.id)
        assertEquals(LocalDate.of(2022, 1, 1), result?.expirationDate)
        assertEquals(listItem.productEan, result?.productEan)
    }

    @Test
    fun `findOrNull should return null if no ListItem is found`() {
        val id = 1L
        every { repository.findByIdOrNull(id) } returns null

        val result = service.findOrNull(id, 0L)

        assertNull(result)
    }

    @Test
    fun `create should create a new ListItem`() {
        val product = Product(ean = EAN("1234567890123"), name = "product")
        val list = InventoryListEntity(id = 0L, name = "myList", userId = UUID.randomUUID())
        val listItem = ListItem(productEan = "1234567890123", expirationDate = LocalDate.of(2022, 1, 1), listId = 0)
        coEvery { apiConnector.findByEan(EAN(listItem.productEan)) } returns product
        every { listRepository.findByIdOrNull(listItem.listId) } returns list
        every { repository.save(any()) } returns ListItemEntity(
            id = 1L,
            expirationDate = LocalDate.of(2022, 1, 1),
            productEan = product.ean.value,
            productName = product.name,
            list = list
        )

        val result = service.create(0L, listItem)

        assertEquals(1L, result.id)
        assertEquals(LocalDate.of(2022, 1, 1), result.expirationDate)
        assertEquals(product.ean.value, result.productEan)
        assertEquals(list.id, result.listId)
    }

    @Test
    fun `create should throw a ResourceNotFoundException if the product is not found`() {
        val listItem = ListItem(expirationDate = LocalDate.of(2022, 1, 1), productEan = "1234567890123", listId = 0L)
        coEvery { apiConnector.findByEan(EAN(listItem.productEan)) } returns null

        assertThrows<ResourceNotFoundException> {
            service.create(0L, listItem)
        }
    }

    @Test
    fun `update should update an existing ListItem`() {
        val id = 1L
        val product = Product(ean = EAN("1234567890123"), name = "product")
        val list = InventoryListEntity(id = 0L, name = "myList", userId = UUID.randomUUID())
        val listItem = ListItem(productEan = "1234567890123", expirationDate = LocalDate.of(2022, 1, 1), listId = 0L)
        val existingItem = ListItemEntity(
            id = id,
            expirationDate = LocalDate.of(2022, 1, 1),
            productEan = product.ean.value,
            productName = product.name,
            list = list
        )
        every { listRepository.findByIdOrNull(list.id) } returns list
        every { repository.findByIdOrNull(id) } returns existingItem
        coEvery { apiConnector.findByEan(EAN(listItem.productEan)) } returns product
        every { repository.save(any()) } returns existingItem.copy(expirationDate = listItem.expirationDate)

        val result = service.update(id, 0L, listItem)

        assertEquals(id, result.id)
        assertEquals(LocalDate.of(2022, 1, 1), result.expirationDate)
        assertEquals(product.ean.value, result.productEan)
        assertEquals(list.id, result.listId)
    }

    @Test
    fun `update should throw a ResourceNotFoundException if the ListItem is not found`() {
        val id = 1L
        val listItem = ListItem(productEan = "1234567890", expirationDate = LocalDate.of(2022, 1, 1), listId = 0L)
        every { repository.findByIdOrNull(id) } returns null

        assertThrows<ResourceNotFoundException> {
            service.update(id, 0L, listItem)
        }
    }

    @Test
    fun `delete should delete the ListItem with the given id and return the deleted item`() {
        val id = 1L
        val list = InventoryListEntity(id = 0L, name = "myList", userId = UUID.randomUUID())
        val existingItem = ListItemEntity(
            id = id,
            expirationDate = LocalDate.of(2022, 1, 1),
            productEan = "1234567890123",
            productName = "product",
            list = list
        )
        every { repository.findByIdOrNull(id) } returns existingItem
        every { repository.deleteById(id) } returns Unit

        val actualItem = service.delete(id)

        verify { repository.deleteById(id) }
        assertEquals(ListItem.fromEntity(existingItem), actualItem)
    }

    @Test
    fun `delete should silently ignore the request and return null when no item with requested Id is found`() {
        val id = 1L
        every { repository.findByIdOrNull(id) } returns null
        every { repository.deleteById(id) } returns Unit

        val actualItem = service.delete(id)

        verify { repository.deleteById(id) }
        assertEquals(null, actualItem)
    }
}
