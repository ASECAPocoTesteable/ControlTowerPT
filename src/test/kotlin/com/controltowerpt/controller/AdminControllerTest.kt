package com.controltowerpt.controller

import com.controltowerpt.controllers.AdminController
import com.controltowerpt.models.Product
import com.controltowerpt.services.ProductService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(AdminController::class)
class AdminControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var productService: ProductService

    @Test
    fun test001createProductSuccessfully() {
        val name = "Test Product"
        val price = 10.0
        val jsonBody = """{"name": "$name", "price": $price}"""

        whenever(productService.createProduct(name, price)).thenReturn(
            Product(
                name = name,
                price = price,
            ),
        )

        mockMvc.perform(
            post("/shop/product/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value(name))
            .andExpect(jsonPath("$.price").value(price))
    }

    @Test
    fun test002createProductWithEmptyName() {
        val name = ""
        val price = 10.0
        val jsonBody = """{"name": "$name", "price": $price}"""

        whenever(productService.createProduct(name, price)).thenThrow(IllegalArgumentException("Product name cannot be empty"))
        mockMvc.perform(
            post("/shop/product/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody),
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().string("Product name cannot be empty"))
    }

    @Test
    fun test003createProductWithNegativePrice() {
        val name = "Test Product"
        val price = -10.0
        val jsonBody = """{"name": "$name", "price": $price}"""

        whenever(productService.createProduct(name, price)).thenThrow(IllegalArgumentException("Product price must be greater than 0"))

        mockMvc.perform(
            post("/shop/product/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody),
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().string("Product price must be greater than 0"))
    }

    @Test
    fun test004deleteProductSuccessfully() {
        val id = 1L
        mockMvc.perform(
            delete("/shop/delete/product")
                .param("id", id.toString()),
        )
            .andExpect(status().isOk)
            .andExpect(content().string("Product deleted"))
    }

    @Test
    fun test005deleteProductWithNegativeId() {
        val id = -1L
        whenever(productService.deleteProduct(id)).thenThrow(IllegalArgumentException("Product id must be greater than 0"))

        mockMvc.perform(
            delete("/shop/delete/product")
                .param("id", id.toString()),
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().string("Product id must be greater than 0"))
    }

    @Test
    fun test006deleteProductIsNotFound() {
        val id = 1L
        whenever(productService.deleteProduct(id)).thenThrow(IllegalArgumentException("Product not found"))

        mockMvc.perform(
            delete("/shop/delete/product")
                .param("id", id.toString()),
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().string("Product not found"))
    }

    @Test
    fun test007updateProductPriceSuccessfully() {
        val id = 0L
        val price = 20.0
        val jsonBody = """{"id": $id, "price": $price}"""

        whenever(productService.updateProductByPrice(price, id)).thenReturn(
            Product(
                name = "Test Product",
                price = price,
            ),
        )

        mockMvc.perform(
            put("/shop/update/product/price")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(id))
            .andExpect(jsonPath("$.price").value(price))
    }

    @Test
    fun test008updateProductPriceIsNegativeShouldError() {
        val id = 0L
        val price = -20.0
        val jsonBody = """{"id": $id, "price": $price}"""

        whenever(productService.updateProductByPrice(price, id)).thenThrow(IllegalArgumentException("Product price must be greater than 0"))

        mockMvc.perform(
            put("/shop/update/product/price")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody),
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().string("Product price must be greater than 0"))
    }

    @Test
    fun test009updateProductPriceIsNotFound() {
        val id = 0L
        val price = 20.0
        val jsonBody = """{"id": $id, "price": $price}"""

        whenever(productService.updateProductByPrice(price, id)).thenThrow(IllegalArgumentException("Product not found"))

        mockMvc.perform(
            put("/shop/update/product/price")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody),
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().string("Product not found"))
    }

//    @Test
//    fun test010GetShopSuccess() {
//        val productList =
//            listOf(
//                Product(name = "Product1", price = 100.0).apply { id = 1L },
//                Product(name = "Product2", price = 200.0).apply { id = 2L },
//            )
//
//        whenever(productService.getAllProducts()).thenReturn(productList)
//
//        mockMvc.perform(get("/shop"))
//            .andExpect(status().isOk)
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//            .andExpect(jsonPath("$.length()").value(productList.size))
//            .andExpect(jsonPath("$[0].id").value(productList[0].id))
//            .andExpect(jsonPath("$[0].name").value(productList[0].name))
//            .andExpect(jsonPath("$[0].price").value(productList[0].price))
//            .andExpect(jsonPath("$[1].id").value(productList[1].id))
//            .andExpect(jsonPath("$[1].name").value(productList[1].name))
//            .andExpect(jsonPath("$[1].price").value(productList[1].price))
//    }

//    @Test
//    fun test011GetShopFailure() {
//        whenever(productService.getAllProducts()).thenThrow(RuntimeException("Internal Server Error"))
//
//        mockMvc.perform(get("/shop"))
//            .andExpect(status().isBadRequest)
//            .andExpect(content().string("Internal Server Error"))
//    }
}
