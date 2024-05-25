package com.controltowerpt.controller

import com.controltowerpt.controllers.ProductController
import com.controltowerpt.models.Product
import com.controltowerpt.models.Shop
import com.controltowerpt.services.ProductService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.util.Optional

@WebMvcTest(ProductController::class)
class ProductControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var productService: ProductService

    @Test
    fun test001createProductSuccessfully() {
        val name = "Test Product"
        val price = 10.0
        val shopId = 1L
        val shop = Shop(name = "Test Shop", shopId)
        val jsonBody = """{"name": "$name", "price": $price, "shopId": $shopId}"""

        whenever(productService.createProduct(name, price, shopId)).thenReturn(
            Product(
                name = name,
                price = price,
                shop = shop,
            ),
        )

        mockMvc.perform(
            post("/product/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value(name))
            .andExpect(jsonPath("$.price").value(price))
            .andExpect(jsonPath("$.shopId").value(shopId.toInt()))
    }

    @Test
    fun test002getProductByIdSuccessfully() {
        val id = 1L
        val name = "Test Product"
        val price = 10.0
        val shopId = 1L
        val shop = Shop(name = "Test Shop", shopId)

        whenever(productService.findProductById(id)).thenReturn(
            Optional.of(
                Product(
                    name = name,
                    price = price,
                    shop = shop,
                ),
            ),
        )

        mockMvc.perform(
            get("/product/get/id")
                .contentType(MediaType.APPLICATION_JSON)
                .content(id.toString()),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value(name))
            .andExpect(jsonPath("$.price").value(price))
            .andExpect(jsonPath("$.shopId").value(shopId.toInt()))
    }

    @Test
    fun test003getProductsByShopIdSuccessfully() {
        val shopId = 1L
        val name = "Test Product"
        val price = 10.0
        val shop = Shop(name = "Test Shop", shopId)

        whenever(productService.findAllByShopId(shopId)).thenReturn(
            listOf(
                Product(
                    name = name,
                    price = price,
                    shop = shop,
                ),
            ),
        )

        mockMvc.perform(
            get("/product/get/shop")
                .contentType(MediaType.APPLICATION_JSON)
                .content(shopId.toString()),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].name").value(name))
            .andExpect(jsonPath("$[0].price").value(price))
            .andExpect(jsonPath("$[0].shopId").value(shopId.toInt()))
    }

    @Test
    fun test004createProductWithEmptyName() {
        val name = ""
        val price = 10.0
        val shopId = 1L
        val jsonBody = """{"name": "$name", "price": $price, "shopId": $shopId}"""

        whenever(productService.createProduct(name, price, shopId)).thenThrow(IllegalArgumentException("Product name cannot be empty"))

        mockMvc.perform(
            post("/product/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody),
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().string("Product name cannot be empty"))
    }
}
