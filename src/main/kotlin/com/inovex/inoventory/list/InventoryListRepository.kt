package com.inovex.inoventory.list

import com.inovex.inoventory.list.domain.InventoryList
import org.springframework.data.jpa.repository.JpaRepository

interface InventoryListRepository : JpaRepository<InventoryList, Long>