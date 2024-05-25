package com.controltowerpt.services

import com.controltowerpt.models.Product
import com.controltowerpt.models.Shop
import com.controltowerpt.repositories.ProductRepository
import com.controltowerpt.servicesImpl.ProductServiceImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class ProductServiceTest {
    private val productRep: ProductRepository = mock()

    private val shopService: ShopService = mock()

    private val productService: ProductService = ProductServiceImpl(productRep, shopService)

    @Test
    fun test001createProduct() {
        val shop = Shop(name = "Test Shop")
        val name = "Test Shop"
        val price = 10.0
        val shopId = 1L
        val product = Product(name = "Test Product", price = 10.0, shop = shop)

        whenever(productRep.save(any(Product::class.java))).thenReturn(product)

        whenever(shopService.findShopById(shopId)).thenReturn(shop)

        productService.createProduct(name, price, shopId)

        verify(productRep).save(any(Product::class.java))
    }

    @Test
    fun test002createProductWithoutNameShouldThrowException() {
        val name = ""
        val price = 10.0
        val shopId = 1L

        val exception =
            assertThrows<IllegalArgumentException> {
                productService.createProduct(name, price, shopId)
            }

        assertEquals("Product name cannot be empty", exception.message)
    }

    @Test
    fun test003createProductWithoutPriceShouldThrowException() {
        val name = "Test Shop"
        val price = 0.0
        val shopId = 1L

        val exception =
            assertThrows<IllegalArgumentException> {
                productService.createProduct(name, price, shopId)
            }

        assertEquals("Product price must be greater than 0", exception.message)
    }

    @Test
    fun test004createProductAndShopDoesNotExistShouldThrowException() {
        val name = "Test Shop"
        val price = 10.0
        val shopId = 1L

        whenever(shopService.findShopById(shopId)).thenReturn(null)

        val exception =
            assertThrows<IllegalArgumentException> {
                productService.createProduct(name, price, shopId)
            }

        assertEquals("Shop not found", exception.message)
    }

    @Test
    fun test005findProductByIdShouldSuccess() {
        val product = Product(name = "Test Product", price = 10.0, shop = Shop(name = "Test Shop"))
        val id = 1L

        whenever(productRep.findById(id)).thenReturn(java.util.Optional.of(product))

        val result = productService.findProductById(id)

        assertEquals(product, result.get())
    }

    @Test
    fun test006findProductByIdShouldReturnEmpty() {
        val id = 1L

        whenever(productRep.findById(id)).thenReturn(java.util.Optional.empty())

        val result = productService.findProductById(id)

        assertEquals(false, result.isPresent)
    }

    @Test
    fun test007findProductByIdIfIsLessThanOneShouldThrowException() {
        val id = 0L

        assertThrows<IllegalArgumentException> {
            productService.findProductById(id)
        }
    }

    @Test
    fun test008getAllProductsFromStore() {
        val shop = Shop(name = "Test Shop")
        val product = Product(name = "Test Product", price = 10.0, shop = shop)
        val products = listOf(product)

        whenever(productRep.findByShopId(1L)).thenReturn(products)

        val result = productService.findAllByShopId(1L)

        assertEquals(products, result)
    }

    @Test
    fun test009updateProductPrice() {
        val product = Product(name = "Test Product", price = 20.0, shop = Shop(name = "Test Shop"))
        val id = 1L
        val newPrice = 20.0

        whenever(productRep.updateProductByPrice(newPrice, id)).thenReturn(20)

        whenever(productRep.findById(id)).thenReturn(java.util.Optional.of(product))

        val result = productService.updateProductByPrice(newPrice, id)

        assertEquals(20.0, result.price)
    }

    @Test
    fun test010updateProductPriceIdIsLessThanOneShouldThrowException() {
        val id = 0L
        val newPrice = 20.0

        assertThrows<IllegalArgumentException> {
            productService.updateProductByPrice(newPrice, id)
        }
    }

    @Test
    fun test011updateProductPriceNewPriceIsLessThanOneShouldThrowException() {
        val id = 1L
        val newPrice = 0.0

        assertThrows<IllegalArgumentException> {
            productService.updateProductByPrice(newPrice, id)
        }
    }

    @Test
    fun test012updateProductPriceProductNotFoundShouldThrowException() {
        val id = 1L
        val newPrice = 20.0

        whenever(productRep.findById(id)).thenReturn(java.util.Optional.empty())

        assertThrows<IllegalArgumentException> {
            productService.updateProductByPrice(newPrice, id)
        }
    }
}
