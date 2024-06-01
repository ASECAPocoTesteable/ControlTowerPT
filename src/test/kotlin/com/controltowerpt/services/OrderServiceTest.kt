package com.controltowerpt.services

import com.controltowerpt.controllers.dto.request.CreateOrderRequest
import com.controltowerpt.controllers.dto.request.ProductQuantity
import com.controltowerpt.models.Order
import com.controltowerpt.models.Product
import com.controltowerpt.models.manytomany.ProductOrder
import com.controltowerpt.repositories.OrderRepository
import com.controltowerpt.repositories.ProductRepository
import com.controltowerpt.servicesImpl.OrderServiceImpl
import com.controltowerpt.servicesImpl.WarehouseService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*
import org.mockito.kotlin.whenever
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class OrderServiceTest {
    private val productRepository: ProductRepository = mock(ProductRepository::class.java)
    private val orderRepository: OrderRepository = mock(OrderRepository::class.java)
    private val warehouseService: WarehouseService = mock(WarehouseService::class.java)
    private var orderService: OrderService = OrderServiceImpl(orderRepository, productRepository, warehouseService)

    @Test
    fun testCreateOrderSuccessfully() {
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
        whenever(warehouseService.checkStock(orderCreateDTO.products)).thenReturn(Mono.just(true))

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

        val createdOrder = orderService.createOrder(orderCreateDTO).block()

        assertNotNull(createdOrder)
        assertEquals("Test Direction", createdOrder?.direction)
        assertEquals(2, createdOrder?.productOrders?.size)

        val productOrder1 = createdOrder?.productOrders?.find { it.product.id == 1L }
        val productOrder2 = createdOrder?.productOrders?.find { it.product.id == 2L }

        assertNotNull(productOrder1)
        assertNotNull(productOrder2)
        assertEquals(1, productOrder1?.amount)
        assertEquals(2, productOrder2?.amount)

        verify(productRepository, times(1)).findById(1L)
        verify(productRepository, times(1)).findById(2L)
        verify(warehouseService, times(1)).checkStock(orderCreateDTO.products)
        verify(orderRepository, times(1)).save(any(Order::class.java))
    }

    @Test
    fun testCreateOrderWithInsufficientStock() {
        val orderCreateDTO =
            CreateOrderRequest(
                direction = "Test Direction",
                products = listOf(ProductQuantity(productId = 1, quantity = 5)),
            )

        whenever(warehouseService.checkStock(orderCreateDTO.products)).thenReturn(Mono.just(false))

        val exception =
            assertThrows(Exception::class.java) {
                orderService.createOrder(orderCreateDTO).block()
            }
        assertEquals("java.lang.Exception: Failed to create order due to: Stock is not sufficient", exception.message)

        verify(warehouseService, times(1)).checkStock(orderCreateDTO.products)
    }

    @Test
    fun testCreateOrderWithWarehouseServiceDown() {
        val orderCreateDTO =
            CreateOrderRequest(
                direction = "Test Direction",
                products = listOf(ProductQuantity(productId = 1, quantity = 1)),
            )

        whenever(warehouseService.checkStock(orderCreateDTO.products)).thenReturn(Mono.error(RuntimeException("Warehouse service down")))

        StepVerifier.create(orderService.createOrder(orderCreateDTO))
            .expectErrorMessage("Failed to create order due to: Retries exhausted: 3/3")
            .verify()

        verify(warehouseService, times(1)).checkStock(orderCreateDTO.products)
    }

    @Test
    fun testCreateOrderWithMultipleConcurrentCalls() {
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
        whenever(warehouseService.checkStock(orderCreateDTO.products)).thenReturn(Mono.just(true))

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

        val latch = CountDownLatch(2)

        val createOrderMono = orderService.createOrder(orderCreateDTO)

        Mono.zip(
            createOrderMono.doOnTerminate { latch.countDown() },
            createOrderMono.doOnTerminate { latch.countDown() },
        ).subscribe()

        assertTrue(latch.await(5, TimeUnit.SECONDS))

        verify(productRepository, times(2)).findById(1L)
        verify(productRepository, times(2)).findById(2L)
        verify(warehouseService, times(1)).checkStock(orderCreateDTO.products)
        verify(orderRepository, times(2)).save(any(Order::class.java))
    }
}
