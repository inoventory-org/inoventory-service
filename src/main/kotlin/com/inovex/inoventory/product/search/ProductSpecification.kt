package com.inovex.inoventory.product.search

import com.inovex.inoventory.product.entity.ProductEntity
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.springframework.data.jpa.domain.Specification

class ProductSpecification(private val searchCriteria: SearchCriteria) : Specification<ProductEntity> {
    override fun toPredicate(
        root: Root<ProductEntity>,
        query: CriteriaQuery<*>,
        criteriaBuilder: CriteriaBuilder
    ): Predicate? = when (searchCriteria.operator) {
        SearchOperator.Equals ->
            criteriaBuilder.equal(root.get<Any>(searchCriteria.field), searchCriteria.value)

        SearchOperator.NotEquals ->
            criteriaBuilder.notEqual(root.get<Any>(searchCriteria.field), searchCriteria.value)

        SearchOperator.GreaterThan -> TODO()
        SearchOperator.LessThan -> TODO()
        SearchOperator.GreaterThanOrEqualTo -> TODO()
        SearchOperator.LessThanOrEqualTo -> TODO()
        SearchOperator.Like -> {
            require(searchCriteria.value is String)
            criteriaBuilder.like(
                criteriaBuilder.lower(root.get(searchCriteria.field)), "%${searchCriteria.value.lowercase()}%"
            )
        }
    }
}