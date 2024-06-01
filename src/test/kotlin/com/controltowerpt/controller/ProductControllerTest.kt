package com.controltowerpt.controller

import com.controltowerpt.controllers.ProductController
import com.controltowerpt.models.Product
import com.controltowerpt.services.ProductService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.util.Optional

@WebMvcTest(ProductController::class)
class ProductControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var productService: ProductService

    @Test
    fun test001getProductByIdSuccessfully() {
        val id = 1L
        val name = "Test Product"
        val price = 10.0

        whenever(productService.findProductById(id)).thenReturn(
            Optional.of(
                Product(
                    name = name,
                    price = price,
                ),
            ),
        )

        mockMvc.perform(
            get("/product/getById?id=$id")
                .contentType(MediaType.APPLICATION_JSON)
                .content(id.toString()),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value(name))
            .andExpect(jsonPath("$.price").value(price))
    }

    @Test
    fun test002findAllProducts() {
        whenever(productService.getAllProducts()).thenReturn(
            listOf(
                Product(
                    name = "Test Product",
                    price = 10.0,
                ),
            ),
        )

        mockMvc.perform(
            get("/product/getAll")
                .contentType(MediaType.APPLICATION_JSON),
        )
            .andExpect(status().isOk)
            .andExpect(content().string("[{\"id\":0,\"name\":\"Test Product\",\"price\":10.0}]"))
    }
}
