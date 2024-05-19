package com.controltowerpt.services

import com.controltowerpt.models.Shop
import com.controltowerpt.repositories.ShopRepository
import com.controltowerpt.servicesImpl.ShopServiceImpl
import jakarta.persistence.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*
import org.mockito.kotlin.whenever
import java.util.Optional

class ShopServiceTest {
    private val shopRepository: ShopRepository = mock()
    private val shopService: ShopService = ShopServiceImpl(shopRepository)

    @Test
    fun test001CreateShop() {
        val shop = Shop(name = "Test Shop")

        `when`(shopRepository.save(any(Shop::class.java))).thenReturn(shop)

        shopService.createShop(shop)

        verify(shopRepository).save(any(Shop::class.java))
    }

    @Test
    fun test002IfNameIsEmptyShouldThrowException() {
        val shop = Shop(name = "")

        val exception =
            assertThrows<IllegalArgumentException> {
                shopService.createShop(shop)
            }

        assertEquals("Shop name cannot be empty", exception.message)
    }

    @Test
    fun test003FindShopByIdShouldBeSuccessful() {
        val shop = Shop(name = "Test Shop")

        `when`(shopRepository.findById(1)).thenReturn(java.util.Optional.of(shop))

        val result = shopService.findShopById(1)

        assertEquals(shop, result)
    }

    @Test
    fun test004FindShopByIdIfShopDosentExistShouldThrowException()  {
        `when`(shopRepository.findById(1)).thenReturn(java.util.Optional.empty())

        val exception =
            assertThrows<EntityNotFoundException> {
                shopService.findShopById(1)
            }

        assertEquals("Shop not found", exception.message)
    }

    @Test
    fun test005DeleteShopByIdShouldBeSuccessful() {
        val shop = Shop(name = "Test Shop")
        `when`(shopRepository.save(any(Shop::class.java))).thenReturn(shop)
        `when`(shopRepository.findById(1)).thenReturn(Optional.of(shop))
        shopService.createShop(shop)
        shopService.deleteShopById(1)
        verify(shopRepository).delete(shop)
    }


    @Test
    fun test006DeleteShopByIdIsNotExistantShouldReturnNull()  {
        `when`(shopRepository.findById(2)).thenReturn(Optional.empty())
        assertThrows<EntityNotFoundException> {
            shopService.deleteShopById(2)
        }
    }

    @Test
    fun test008DeleteShopByIdWhenIdIsLessThanOneShouldThrowException()  {
        val shop = Shop(name = "Test Shop")
        `when`(shopRepository.save(any(Shop::class.java))).thenReturn(shop)
        val savedShop = shopService.createShop(shop)
        val exception =
            assertThrows<IllegalArgumentException> {
                shopService.deleteShopById(0)
            }
        assertEquals("Shop id must be greater than 0", exception.message)
    }
}
