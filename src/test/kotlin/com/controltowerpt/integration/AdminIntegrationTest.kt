package com.controltowerpt.integration

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest
@ActiveProfiles("integration")
@AutoConfigureMockMvc
class AdminIntegrationTest {
    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Test
    fun test001createProductSuccessfully() {
        val name = "Test Product"
        val price = 10.0
        val jsonBody = """{"name": "$name", "price": $price}"""

        webTestClient.post().uri("/shop/product/add")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(jsonBody)
            .exchange()
            .expectStatus().isOk
    }

    @Test
    fun test003createProductWithNegativePrice() {
        val name = "Test Product"
        val price = -10.0
        val jsonBody = """{"name": "$name", "price": $price}"""

        webTestClient.post().uri("/shop/product/add")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(jsonBody)
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun test004deleteProductSuccessfully() {
        val name = "Test Product"
        val price = 10.0
        val jsonBody = """{"name": "$name", "price": $price}"""

        val result =
            webTestClient.post().uri("/shop/product/add")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(jsonBody)
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$.id").isNotEmpty
                .returnResult()
                .responseBody

        val id = extractIdFromResponse(result)

        webTestClient.delete().uri("/shop/product/delete/$id")
            .exchange()
            .expectStatus().isOk
    }

    private fun extractIdFromResponse(response: ByteArray?): Long {
        val responseString = response?.let { String(it) } ?: ""
        val jsonNode = ObjectMapper().readTree(responseString)
        return jsonNode.get("id").asLong()
    }
}
