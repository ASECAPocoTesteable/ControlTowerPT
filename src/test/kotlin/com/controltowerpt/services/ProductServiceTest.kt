package com.controltowerpt.services

import com.controltowerpt.models.Product
import com.controltowerpt.repositories.ProductRepository
import com.controltowerpt.servicesImpl.ProductServiceImpl
import com.controltowerpt.servicesImpl.WarehouseService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class ProductServiceTest {
    private val productRep: ProductRepository = mock()
    private val warehouseService: WarehouseService = mock()

    private val productService: ProductService = ProductServiceImpl(productRep, warehouseService)

    @Test
    fun test001createProduct() {
        val name = "Test Shop"
        val price = 10.0
        val product = Product(name = "Test Product", price = 10.0)

        whenever(productRep.save(any(Product::class.java))).thenReturn(product)

        productService.createProduct(name, price)

        verify(productRep).save(any(Product::class.java))
    }

    @Test
    fun test002createProductWithoutNameShouldThrowException() {
        val name = ""
        val price = 10.0

        val exception =
            assertThrows<IllegalArgumentException> {
                productService.createProduct(name, price)
            }

        assertEquals("Product name cannot be empty", exception.message)
    }

    @Test
    fun test003createProductWithoutPriceShouldThrowException() {
        val name = "Test Shop"
        val price = 0.0

        val exception =
            assertThrows<IllegalArgumentException> {
                productService.createProduct(name, price)
            }

        assertEquals("Product price must be greater than 0", exception.message)
    }

    @Test
    fun test004findProductByIdShouldSuccess() {
        val product = Product(name = "Test Product", price = 10.0)
        val id = 1L

        whenever(productRep.findById(id)).thenReturn(java.util.Optional.of(product))

        val result = productService.findProductById(id)

        assertEquals(product, result.get())
    }

    @Test
    fun test005findProductByIdShouldReturnEmpty() {
        val id = 1L

        whenever(productRep.findById(id)).thenReturn(java.util.Optional.empty())

        val result = productService.findProductById(id)

        assertEquals(false, result.isPresent)
    }

    @Test
    fun test006findProductByIdIfIsLessThanOneShouldThrowException() {
        val id = 0L

        assertThrows<IllegalArgumentException> {
            productService.findProductById(id)
        }
    }

    @Test
    fun test007updateProductPrice() {
        val product = Product(name = "Test Product", price = 20.0)
        val id = 1L
        val newPrice = 20.0

        whenever(productRep.updateProductByPrice(newPrice, id)).thenReturn(20)

        whenever(productRep.findById(id)).thenReturn(java.util.Optional.of(product))

        val result = productService.updateProductByPrice(newPrice, id)

        assertEquals(20.0, result.price)
    }

    @Test
    fun test008updateProductPriceIdIsLessThanOneShouldThrowException() {
        val id = 0L
        val newPrice = 20.0

        assertThrows<IllegalArgumentException> {
            productService.updateProductByPrice(newPrice, id)
        }
    }

    @Test
    fun test009updateProductPriceNewPriceIsLessThanOneShouldThrowException() {
        val id = 1L
        val newPrice = 0.0

        assertThrows<IllegalArgumentException> {
            productService.updateProductByPrice(newPrice, id)
        }
    }

    @Test
    fun test010updateProductPriceProductNotFoundShouldThrowException() {
        val id = 1L
        val newPrice = 20.0

        whenever(productRep.findById(id)).thenReturn(java.util.Optional.empty())

        assertThrows<IllegalArgumentException> {
            productService.updateProductByPrice(newPrice, id)
        }
    }

    @Test
    fun test011getAllProducts() {
        val product = Product(name = "Test Product", price = 10.0)
        val products = listOf(product)

        whenever(productRep.findAll()).thenReturn(products)

        val result = productService.getAllProducts()

        assertEquals(products, result)
    }

    @Test
    fun test012deleteProductSuccessfully() {
        val id = 1L

        whenever(productRep.findById(id)).thenReturn(java.util.Optional.of(Product(name = "Test Product", price = 10.0)))

        productService.deleteProduct(id)

        verify(productRep).deleteById(id)
    }

    @Test
    fun test013deleteProductIdNegativeShouldThrowException() {
        val id = -1L

        assertThrows<IllegalArgumentException> {
            productService.deleteProduct(id)
        }
    }

    @Test
    fun test014deleteProductNotFoundShouldThrowException() {
        val id = 1L

        whenever(productRep.findById(id)).thenReturn(java.util.Optional.empty())

        assertThrows<IllegalArgumentException> {
            productService.deleteProduct(id)
        }
    }
}
