package com.controltowerpt.integration

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.util.Assert

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration")
@AutoConfigureMockMvc
class AdminIntegrationTest {
    @LocalServerPort
    private val port: Int = 0

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    var createdProductId: Long? = null

    @Test
    fun test001createProductSuccessfully() {
        val name = "Test Product"
        val price = 10.0
        val jsonBody = """{"name": "$name", "price": $price}"""

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON

        val entity = HttpEntity(jsonBody, headers)

        val response = restTemplate.postForEntity("http://localhost:$port/shop/product/add", entity, String::class.java)

        createdProductId = response.body?.substringAfter("id\":")?.substringBefore(",")?.toLong()

        Assert.isTrue(response.statusCode.is2xxSuccessful, "Expected successful status code")

        cleanupAfterTest()
    }

    @Test
    fun test002createProductWithEmptyName() {
        val name = ""
        val price = 10.0
        val jsonBody = """{"name": "$name", "price": $price}"""

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON

        val entity = HttpEntity(jsonBody, headers)

        val response = restTemplate.postForEntity("http://localhost:$port/shop/product/add", entity, String::class.java)

        Assert.isTrue(response.statusCode.is4xxClientError, "Expected client error status code")
    }

    @Test
    fun test003createProductWithNegativePrice() {
        val name = "Test Product"
        val price = -10.0
        val jsonBody = """{"name": "$name", "price": $price}"""

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON

        val entity = HttpEntity(jsonBody, headers)

        val response = restTemplate.postForEntity("http://localhost:$port/shop/product/add", entity, String::class.java)

        Assert.isTrue(response.statusCode.is4xxClientError, "Expected client error status code")
    }

    private fun cleanupAfterTest() {
        createdProductId?.let { id ->
            restTemplate.delete("http://localhost:$port/shop/delete/product?id=$id")
        }
    }
}
