package com.inovex.inoventory.product.search

import com.inovex.inoventory.product.entity.ProductEntity

@JvmInline
value class SearchString(private val value: String) {
    init {
        require(regex.matches(value))
    }

    fun extractSearchCriteria(): List<SearchCriteria> =
        regex.findAll(value).map {
            val field = it.groups[1]!!.value
            val operator = SearchOperator.values().single { s -> s.representation == it.groups[2]!!.value }
            val searchValue = it.groups[3]!!.value
            SearchCriteria(field, searchValue, operator)
        }.toList()

    companion object {
        val regex = buildRegex()
        private fun buildRegex(): Regex {
            val fields = ProductEntity::class.java.declaredFields.joinToString("|") { it.name }
            val operators = SearchOperator.values().joinToString("|") { it.representation }
            val values = "([a-zA-Z0-9äöüÄÖÜß]|\\s|%)*"

            return "($fields)($operators)($values)".toRegex()
        }
    }
}