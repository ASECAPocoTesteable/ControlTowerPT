package com.controltowerpt.services

import com.controltowerpt.controllers.dto.request.CreateOrderRequest
import com.controltowerpt.controllers.dto.request.NewDeliveryData
import com.controltowerpt.controllers.dto.request.ProductQuantity
import com.controltowerpt.models.Order
import com.controltowerpt.models.OrderState
import com.controltowerpt.models.Product
import com.controltowerpt.models.Warehouse
import com.controltowerpt.models.manytomany.ProductOrder
import com.controltowerpt.repositories.OrderRepository
import com.controltowerpt.repositories.ProductRepository
import com.controltowerpt.servicesImpl.DeliveryService
import com.controltowerpt.servicesImpl.OrderServiceImpl
import com.controltowerpt.servicesImpl.WarehouseService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.any
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
    private var deliveryService: DeliveryService = mock(DeliveryService::class.java)
    private var orderService: OrderService = OrderServiceImpl(orderRepository, productRepository, warehouseService, deliveryService)

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
        whenever(warehouseService.checkStock(orderCreateDTO.products)).thenReturn(Mono.just(true))

        val order =
            Order(clientDirection = "Test Direction").apply {
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
        assertEquals("Test Direction", createdOrder?.clientDirection)
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
    fun test002CreateOrderWithInsufficientStock() {
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
    fun test003CreateOrderWithWarehouseServiceDown() {
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
    fun test004CreateOrderWithMultipleConcurrentCalls() {
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
            Order(clientDirection = "Test Direction").apply {
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

    @Test
    fun test005GetAllOrders() {
        val order1 = Order(clientDirection = "Test Direction 1").apply { id = 1L }
        val order2 = Order(clientDirection = "Test Direction 2").apply { id = 2L }
        val order3 = Order(clientDirection = "Test Direction 3").apply { id = 3L }

        whenever(orderRepository.findAll()).thenReturn(listOf(order1, order2, order3))

        val orders = orderService.getAllOrders()

        assertEquals(3, orders.size)
        assertEquals("Test Direction 1", orders[0].clientDirection)
        assertEquals("Test Direction 2", orders[1].clientDirection)
        assertEquals("Test Direction 3", orders[2].clientDirection)

        verify(orderRepository, times(1)).findAll()
    }

    @Test
    fun test006OrderIsReadyShouldReturnTrueAndUpdateOrderStateWhenDeliveryServiceIsSuccessful() {
        val product1 = Product(name = "Product1", price = 100.0).apply { id = 1L }
        val product2 = Product(name = "Product2", price = 200.0).apply { id = 2L }

        val productOrder1 = ProductOrder(product = product1, order = Order(), amount = 1)
        val productOrder2 = ProductOrder(product = product2, order = Order(), amount = 2)

        val order =
            Order(
                clientDirection = "Client Direction",
                state = OrderState.PREPARING,
                productOrders = mutableListOf(productOrder1, productOrder2),
            ).apply {
                id = 1L
                warehouse = Warehouse().apply { direction = "Warehouse Direction" }
            }

        productOrder1.order = order
        productOrder2.order = order

        whenever(orderRepository.findById(1L)).thenReturn(Optional.of(order))
        whenever(deliveryService.initializeDelivery(any())).thenReturn(Mono.just(true))
        whenever(orderRepository.save(any<Order>())).thenReturn(order.apply { state = OrderState.IN_DELIVERY })

        val result = orderService.orderIsReady(1L)

        StepVerifier.create(result)
            .expectNext(true)
            .verifyComplete()

        verify(orderRepository).findById(1L)
        verify(deliveryService).initializeDelivery(any())
        verify(orderRepository).save(any<Order>())
    }

    @Test
    fun test007OrderIsReadyShouldReturnFalseWhenDeliveryServiceFails() {
        val product1 = Product(name = "Product1", price = 100.0).apply { id = 1L }
        val product2 = Product(name = "Product2", price = 200.0).apply { id = 2L }

        val productOrder1 = ProductOrder(product = product1, order = Order(), amount = 1)
        val productOrder2 = ProductOrder(product = product2, order = Order(), amount = 2)

        val order =
            Order(
                clientDirection = "Client Direction",
                state = OrderState.PREPARING,
                productOrders = mutableListOf(productOrder1, productOrder2),
            ).apply {
                id = 1L
                warehouse = Warehouse().apply { direction = "Warehouse Direction" }
            }

        productOrder1.order = order
        productOrder2.order = order

        whenever(orderRepository.findById(1L)).thenReturn(Optional.of(order))
        whenever(deliveryService.initializeDelivery(any<NewDeliveryData>())).thenReturn(Mono.just(false))

        val result = orderService.orderIsReady(1L)

        StepVerifier.create(result)
            .expectNext(false)
            .verifyComplete()

        verify(orderRepository).findById(1L)
        verify(deliveryService).initializeDelivery(any<NewDeliveryData>())
        verify(orderRepository, never()).save(any<Order>())
    }

    @Test
    fun test008OrderIsReadyShouldReturnErrorWhenOrderIsNotFound() {
        whenever(orderRepository.findById(1L)).thenReturn(Optional.empty())

        val result = orderService.orderIsReady(1L)

        StepVerifier.create(result)
            .expectErrorMatches { it is IllegalArgumentException && it.message == "Order with id 1 not found" }
            .verify()

        verify(orderRepository).findById(1L)
        verify(deliveryService, never()).initializeDelivery(any())
        verify(orderRepository, never()).save(any())
    }

    @Test
    fun test009CreateOrderWithEmptyDirection() {
        val createOrderRequest = CreateOrderRequest(direction = "", products = listOf(ProductQuantity(1L, 1)))

        val result = orderService.createOrder(createOrderRequest)

        StepVerifier.create(result)
            .expectErrorMatches { it is IllegalArgumentException && it.message == "Direction cannot be empty" }
            .verify()
    }

    @Test
    fun test010CreateOrderWithEmptyProducts() {
        val createOrderRequest = CreateOrderRequest(direction = "Some Direction", products = emptyList())

        val result = orderService.createOrder(createOrderRequest)

        StepVerifier.create(result)
            .expectErrorMatches { it is IllegalArgumentException && it.message == "Products cannot be empty" }
            .verify()
    }

    @Test
    fun test011CreateOrderWithInvalidProductID() {
        val createOrderRequest = CreateOrderRequest(direction = "Some Direction", products = listOf(ProductQuantity(0L, 1)))

        val result = orderService.createOrder(createOrderRequest)

        StepVerifier.create(result)
            .expectErrorMatches { it is IllegalArgumentException && it.message == "Product id must be greater than 0" }
            .verify()
    }

    @Test
    fun test012CreateOrderWithInvalidProductQuantity() {
        val createOrderRequest = CreateOrderRequest(direction = "Some Direction", products = listOf(ProductQuantity(1L, 0)))

        val result = orderService.createOrder(createOrderRequest)

        StepVerifier.create(result)
            .expectErrorMatches { it is IllegalArgumentException && it.message == "Product quantity must be greater than 0" }
            .verify()
    }

    @Test
    fun test013CreateOrderWithInsufficientStock() {
        val createOrderRequest = CreateOrderRequest(direction = "Some Direction", products = listOf(ProductQuantity(1L, 1)))

        whenever(warehouseService.checkStock(any())).thenReturn(Mono.just(false))

        val result = orderService.createOrder(createOrderRequest)

        StepVerifier.create(result)
            .expectErrorMatches {
                it is Exception &&
                    it.message?.contains("Stock is not sufficient") == true
            }
            .verify()
    }

    @Test
    fun test014OrderIsReadyWithInvalidOrderId() {
        val invalidOrderId = 0L

        val result = orderService.orderIsReady(invalidOrderId)

        StepVerifier.create(result)
            .expectErrorMatches {
                it is IllegalArgumentException &&
                    it.message == "Order id must be greater than 0"
            }
            .verify()
    }
}
