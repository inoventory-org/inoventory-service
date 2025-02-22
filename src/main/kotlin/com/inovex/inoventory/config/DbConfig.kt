package com.inovex.inoventory.config

import com.inovex.inoventory.list.InventoryListRepository
import com.inovex.inoventory.list.InventoryListService
import com.inovex.inoventory.list.entity.InventoryListEntity
import com.inovex.inoventory.list.item.ListItemRepository
import com.inovex.inoventory.list.item.entity.ListItemEntity
import com.inovex.inoventory.list.permission.PermissionService
import com.inovex.inoventory.list.permission.entity.AccessRight
import com.inovex.inoventory.notification.NotificationRepository
import com.inovex.inoventory.notification.entity.NotificationEntity
import com.inovex.inoventory.product.ProductRepository
import com.inovex.inoventory.product.entity.ProductEntity
import com.inovex.inoventory.product.entity.SourceEntity
import com.inovex.inoventory.user.UserRepository
import com.inovex.inoventory.user.entity.UserEntity
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import java.time.LocalDate
import java.util.*

@Configuration
class DbConfig {

    @Bean
    @Profile("!test")
    fun initDb(
        userRepository: UserRepository,
        notificationRepository: NotificationRepository,
        productRepository: ProductRepository,
        listRepository: InventoryListRepository,
        listItemRepository: ListItemRepository,
        permissionService: PermissionService
    ) = CommandLineRunner {
        val testUserName = "luke.skywalker"
        var user = userRepository.findByUserName(testUserName)
        if (user == null) {
            user = userRepository.save(UserEntity(id = UUID.fromString("328d58cc-1973-4e53-99d4-f49e8b48dc60"), userName = "luke.skywalker"))
            var product =
                ProductEntity(name = "product", brands = "Some-Brand", ean = "12345678", source = SourceEntity.USER)
            var product2 = ProductEntity(name = "product2", ean = "98765432", source = SourceEntity.USER)
            product = productRepository.save(product)
            product2 = productRepository.save(product2)

            var list = InventoryListEntity(id = 0L, name = "myList", user = user)
            list = listRepository.save(list)

            permissionService.createPermissions(user.id, list.id!!, AccessRight.entries)

            var listItem1 =
                ListItemEntity(id = 1L, expirationDate = LocalDate.of(2022, 1, 2), product = product, list = list)
            var listItem2 =
                ListItemEntity(id = 2L, expirationDate = LocalDate.of(2022, 1, 2), product = product, list = list)
            var listItem3 =
                ListItemEntity(id = 2L, expirationDate = LocalDate.of(2022, 1, 2), product = product2, list = list)

            listItem1 = listItemRepository.save(listItem1)
            listItem2 = listItemRepository.save(listItem2)
            listItem3 = listItemRepository.save(listItem3)

            if (notificationRepository.count() == 0L) {
                val notifications = listOf(
                    NotificationEntity(date = LocalDate.now(), item = listItem1),
                    NotificationEntity(date = LocalDate.of(2025, 3, 28), item = listItem2),
                    NotificationEntity(date = LocalDate.of(2025, 3, 28), item = listItem3)
                )
                notificationRepository.saveAll(notifications)
                println("Database initialized with test data.")
            }
        }
    }

}