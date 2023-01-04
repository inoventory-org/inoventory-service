package com.inovex.inoventory.product.search

@JvmInline
value class SearchString(val value: String) {
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
        val regex = "([a-zA-Z0-9äöüÄÖÜß]*)(${
            SearchOperator.values().joinToString("|") { it.representation }
        })(([a-zA-Z0-9äöüÄÖÜß]|\\s|%)*)".toRegex()
    }
}