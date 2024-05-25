package com.controltowerpt.controller

import com.controltowerpt.controllers.ProductController
import com.controltowerpt.models.Shop
import com.controltowerpt.services.ProductService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.MockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(ProductController::class)
class ProductControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var productService: ProductService

    @Test
    fun test001createProductSuccessfully() {
        val name = "Test Product"
        val price = 10.0
        val shopId = 1L
        val shop = Shop()
        val jsonBody = """{"name": "$name", "price": $price, "shopId": $shopId}"""

        whenever(productService.createProduct(name, price, shopId)).thenReturn(
            com.controltowerpt.models.Product(
                name = name,
                price = price,
                shopId = shop
            ))

        mockMvc.perform(
            post("/product/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value(name))
            .andExpect(jsonPath("$.price").value(price))
            .andExpect(jsonPath("$.shopId").value(shopId))
    }


}