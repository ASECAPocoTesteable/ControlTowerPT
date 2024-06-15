package com.controltowerpt.integration

import com.controltowerpt.controllers.AdminController
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.transaction.annotation.Transactional
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest
@ActiveProfiles("integration")
@AutoConfigureMockMvc
class AdminIntegrationTest{

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun test001createProductSuccessfully() {
        val name = "Test Product"
        val price = 10.0
        val jsonBody = """{"name": "$name", "price": $price}"""

        mockMvc.perform(
            post("/shop/product/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody),
        )
            .andExpect(status().isOk)
    }
}
