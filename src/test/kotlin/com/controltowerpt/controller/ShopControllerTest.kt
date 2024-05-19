package com.controltowerpt.controller

import com.controltowerpt.controllers.ShopController
import com.controltowerpt.models.Shop
import com.controltowerpt.services.ShopService
import jakarta.persistence.*
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(ShopController::class)
class ShopControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var shopService: ShopService

    @Test
    fun test001PostSaveShopSuccess() {
        val shopName = "Test Shop"
        val shop = Shop(name = shopName)

        // Mock behavior of shopService.createShop
        whenever(shopService.createShop(any())).thenReturn(shop)
        val jsonBody = """{"shopName": "$shopName"}"""

        mockMvc.perform(
            post("/shop/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value(shopName))
    }

    @Test
    fun test002NameIsEmptyShouldThrowError() {
        val jsonBody = """{"shopName": ""}"""
        whenever(shopService.createShop(any())).thenThrow(IllegalArgumentException("Shop name cannot be empty"))

        mockMvc.perform(
            post("/shop/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody),
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().string("Shop name cannot be empty"))
    }

    @Test
    fun test003GetShopByIdSuccess(){
        val shopName = "Test Shop"
        val shop = Shop(name = shopName)
        val shopId = 1L

        whenever(shopService.findShopById(shopId)).thenReturn(shop)

        mockMvc.perform(
            get("/shop/get")
                .param("id", shopId.toString()),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value(shopName))
    }

    @Test
    fun test004GetShopByIdNotFoundShouldBadRequest(){
        val shopId = 1L
        whenever(shopService.findShopById(shopId)).thenThrow(EntityNotFoundException("Shop not found"))

        mockMvc.perform(
            get("/shop/get")
                .param("id", shopId.toString()),
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().string("Shop not found"))
    }

    @Test
    fun test005GetShopByIdWhenIdIsLowerThan0ShouldBadRequest(){
        val shopId = -1L
        whenever(shopService.findShopById(shopId)).thenThrow(IllegalArgumentException("Shop id must be greater than 0"))

        mockMvc.perform(
            get("/shop/get")
                .param("id", shopId.toString()),
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().string("Shop id must be greater than 0"))
    }
}
