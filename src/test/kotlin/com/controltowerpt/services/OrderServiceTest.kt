package com.controltowerpt.services

import com.controltowerpt.controllers.dto.request.CreateOrderRequest
import com.controltowerpt.controllers.dto.request.ProductQuantity
import com.controltowerpt.models.Order
import com.controltowerpt.models.Product
import com.controltowerpt.models.manytomany.ProductOrder
import com.controltowerpt.repositories.OrderRepository
import com.controltowerpt.repositories.ProductRepository
import com.controltowerpt.servicesImpl.OrderServiceImpl
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.whenever
import java.util.*

class OrderServiceTest {
    private val productRepository: ProductRepository = mock()

    private val orderRepository: OrderRepository = mock()

    private var orderService: OrderService = OrderServiceImpl(orderRepository, productRepository)

    @Test
    fun test001CreateOrderSuccessfully() {
        val orderCreateDTO =
            CreateOrderRequest(
                direction = "Test Direction",
                products =
                    listOf(
                        ProductQuantity(productId = 1, quantity = 1),
                        ProductQuantity(productId = 2, quantity = 2),
                    ),
            )

        val product1 = Product("Product1", 100.0).apply { id = 1L }
        val product2 = Product("Product2", 200.0).apply { id = 2L }

        whenever(productRepository.findById(1L)).thenReturn(Optional.of(product1))
        whenever(productRepository.findById(2L)).thenReturn(Optional.of(product2))

        val order =
            Order(direction = "Test Direction").apply {
                id = 1L
                productOrders =
                    mutableListOf(
                        ProductOrder(product = product1, order = this, amount = 1),
                        ProductOrder(product = product2, order = this, amount = 2),
                    )
            }

        whenever(orderRepository.save(any(Order::class.java))).thenReturn(order)

        val createdOrder = orderService.createOrder(orderCreateDTO)

        assertNotNull(createdOrder)
        assertEquals("Test Direction", createdOrder.direction)
        assertEquals(2, createdOrder.productOrders.size)

        val productOrder1 = createdOrder.productOrders.find { it.product.id == 1L }
        val productOrder2 = createdOrder.productOrders.find { it.product.id == 2L }

        assertNotNull(productOrder1)
        assertNotNull(productOrder2)
        assertEquals(1, productOrder1?.amount)
        assertEquals(2, productOrder2?.amount)

        verify(productRepository, times(1)).findById(1L)
        verify(productRepository, times(1)).findById(2L)
        verify(orderRepository, times(1)).save(any(Order::class.java))
    }

    @Test
    fun test002CreateOrderWithEmptyProducts() {
        val orderCreateDTO =
            CreateOrderRequest(
                direction = "Test Direction",
                products = emptyList(),
            )

        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                orderService.createOrder(orderCreateDTO)
            }
        assertEquals("Products cannot be empty", exception.message)
    }

    @Test
    fun test003CreateOrderWithInvalidProductId() {
        val orderCreateDTO =
            CreateOrderRequest(
                direction = "Test Direction",
                products = listOf(ProductQuantity(productId = 0, quantity = 1)),
            )

        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                orderService.createOrder(orderCreateDTO)
            }
        assertEquals("Product id must be greater than 0", exception.message)
    }

    @Test
    fun test004CreateOrderWithInvalidProductQuantity() {
        val orderCreateDTO =
            CreateOrderRequest(
                direction = "Test Direction",
                products = listOf(ProductQuantity(productId = 1, quantity = 0)),
            )

        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                orderService.createOrder(orderCreateDTO)
            }
        assertEquals("Product quantity must be greater than 0", exception.message)
    }

    @Test
    fun test005CreateOrderWithNonexistentProduct() {
        val orderCreateDTO =
            CreateOrderRequest(
                direction = "Test Direction",
                products = listOf(ProductQuantity(productId = 1, quantity = 1)),
            )

        whenever(productRepository.findById(1L)).thenReturn(Optional.empty())

        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                orderService.createOrder(orderCreateDTO)
            }
        assertEquals("Product with id 1 not found", exception.message)
    }
}
