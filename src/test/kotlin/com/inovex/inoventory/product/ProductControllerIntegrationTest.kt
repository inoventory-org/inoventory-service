package com.inovex.inoventory.product

import com.c4_soft.springaddons.security.oauth2.test.annotations.WithMockJwtAuth
import com.fasterxml.jackson.databind.ObjectMapper
import com.inovex.inoventory.product.dto.EAN
import com.inovex.inoventory.product.dto.Product
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import spock.lang.Specification

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
internal class ProductControllerIntegrationTest : Specification() {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Test
    @WithMockJwtAuth(authorities = ["inoventory-user"])
    fun `GET product works`() {
        val expected = listOf(
            Product(name = "MyProduct1", ean = EAN("01234567")),
            Product(name = "MyProduct2", ean = EAN("12345678"))
        )
        expected.forEach {
            println(objectMapper.writeValueAsString(it))
            mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(it))
            )
        }

        val result = mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/products")
                .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk).andReturn()

        val actual = objectMapper.readValue(result.response.contentAsString, Array<Product>::class.java).toList()

        assertEquals(expected, actual)
    }
}