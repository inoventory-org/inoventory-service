package com.inovex.inoventory.list

import com.inovex.inoventory.list.domain.InventoryList
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface InventoryListRepository : JpaRepository<InventoryList, Long> {
    fun findByUserId(userId: UUID): List<InventoryList>
}