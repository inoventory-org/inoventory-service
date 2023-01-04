package com.inovex.inoventory.product.search

enum class SearchOperator(val representation: String){
    Equals("="),
    NotEquals("!="),
    GreaterThan(">"),
    LessThan("<"),
    GreaterThanOrEqualTo(">="),
    LessThanOrEqualTo("<="),
    Like("~"),
}