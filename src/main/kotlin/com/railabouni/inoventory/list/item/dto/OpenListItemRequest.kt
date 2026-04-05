package com.railabouni.inoventory.list.item.dto

import java.time.LocalDate

data class OpenListItemRequest(
    val expirationDate: LocalDate? = null,
    val openedAt: LocalDate? = null
)
