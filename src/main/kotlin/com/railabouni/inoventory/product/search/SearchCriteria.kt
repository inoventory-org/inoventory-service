package com.railabouni.inoventory.product.search

data class SearchCriteria(
    val field : String,
    val value : Any,
    val operator: SearchOperator
)

