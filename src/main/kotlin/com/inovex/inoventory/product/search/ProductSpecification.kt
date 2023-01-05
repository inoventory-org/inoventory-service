package com.inovex.inoventory.product.search

import com.inovex.inoventory.exceptions.InvalidSearchOperationException
import com.inovex.inoventory.product.entity.ProductEntity
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.springframework.data.jpa.domain.Specification
import java.time.Instant
import java.time.LocalDate

class ProductSpecification(private val searchCriteria: SearchCriteria) : Specification<ProductEntity> {
    override fun toPredicate(
        root: Root<ProductEntity>,
        query: CriteriaQuery<*>,
        criteriaBuilder: CriteriaBuilder
    ): Predicate {
        return when (ProductEntity::class.java.declaredFields.single { it.name == searchCriteria.field }.type) {
            String::class.java -> compareString(searchCriteria.value as String, root, criteriaBuilder)
            Int::class.java -> compare(searchCriteria.value as Int, root, criteriaBuilder)
            Double::class.java -> compare(searchCriteria.value as Double, root, criteriaBuilder)
            LocalDate::class.java -> compare(searchCriteria.value as LocalDate, root, criteriaBuilder)
            Instant::class.java -> compare(searchCriteria.value as Instant, root, criteriaBuilder)
            else -> throw InvalidSearchOperationException(
                "Invalid search criteria: must be in ${allowedComparableTypes.joinToString { it.name }}"
            )
        }
    }

    private fun <T : Comparable<T>> compare(
        value: T,
        root: Root<ProductEntity>,
        criteriaBuilder: CriteriaBuilder
    ): Predicate = when (searchCriteria.operator) {
        SearchOperator.Equals ->
            criteriaBuilder.equal(root.get<T>(searchCriteria.field), value)

        SearchOperator.NotEquals ->
            criteriaBuilder.notEqual(root.get<T>(searchCriteria.field), value)

        SearchOperator.GreaterThan ->
            criteriaBuilder.greaterThan(root.get(searchCriteria.field), value)

        SearchOperator.LessThan ->
            criteriaBuilder.lessThan(root.get(searchCriteria.field), value)

        SearchOperator.GreaterThanOrEqualTo ->
            criteriaBuilder.greaterThanOrEqualTo(root.get(searchCriteria.field), value)

        SearchOperator.LessThanOrEqualTo ->
            criteriaBuilder.lessThanOrEqualTo(root.get(searchCriteria.field), value)

        SearchOperator.Like -> {
            require(value is String)
            criteriaBuilder.like(
                criteriaBuilder.lower(root.get(searchCriteria.field)), "%${value.lowercase()}%"
            )
        }
    }

    private fun compareString(value: String, root: Root<ProductEntity>, criteriaBuilder: CriteriaBuilder): Predicate {
        val compareValue = value.lowercase()
        return when (searchCriteria.operator) {
            SearchOperator.Equals ->
                criteriaBuilder.equal(criteriaBuilder.lower(root.get(searchCriteria.field)), compareValue)

            SearchOperator.NotEquals ->
                criteriaBuilder.notEqual(criteriaBuilder.lower(root.get(searchCriteria.field)), compareValue)

            SearchOperator.Like ->
                criteriaBuilder.like(
                    criteriaBuilder.lower(root.get(searchCriteria.field)), "%${compareValue}%"
                )

            else -> {
                val allowedOperators = listOf(SearchOperator.Equals, SearchOperator.NotEquals, SearchOperator.Like)
                throw InvalidSearchOperationException(
                    "${searchCriteria.operator} is not a valid string operator. " +
                            "Allowed operators are: ${allowedOperators.joinToString { it.representation }}"
                )
            }
        }
    }

    companion object {
        private val allowedComparableTypes = listOf(
            String::class.java,
            Int::class.java,
            Double::class.java,
            LocalDate::class.java,
            Instant::class.java
        )
    }
}