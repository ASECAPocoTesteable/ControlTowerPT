package com.controltowerpt.controllers

import com.controltowerpt.controllers.dto.request.CreateOrderRequest
import com.controltowerpt.controllers.dto.request.ProductQuantity
import com.controltowerpt.models.Order
import com.controltowerpt.services.OrderService
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono

@WebFluxTest(OrderController::class)
class OrderControllerTest {
    @Autowired
    private lateinit var webTestClient: WebTestClient

    @MockBean
    private lateinit var orderService: OrderService

    private val objectMapper = jacksonObjectMapper()

    @BeforeEach
    fun setUp() {
        Mockito.reset(orderService)
    }

    @Test
    fun test001CheckoutCartShouldReturn200WhenOrderIsCreatedSuccessfully() {
        val createOrderRequest =
            CreateOrderRequest(
                direction = "Test Direction",
                products =
                    listOf(
                        ProductQuantity(productId = 1L, quantity = 1),
                        ProductQuantity(productId = 2L, quantity = 2),
                    ),
            )

        val order = Order(direction = "Test Direction")

        whenever(orderService.createOrder(createOrderRequest)).thenReturn(Mono.just(order))

        webTestClient.post().uri("/order/checkout")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(objectMapper.writeValueAsString(createOrderRequest))
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.direction").isEqualTo("Test Direction")
    }

    @Test
    fun test002CheckoutCartShouldReturn400WhenOrderCreationFails() {
        val createOrderRequest =
            CreateOrderRequest(
                direction = "Test Direction",
                products =
                    listOf(
                        ProductQuantity(productId = 1L, quantity = 1),
                        ProductQuantity(productId = 2L, quantity = 2),
                    ),
            )

        whenever(orderService.createOrder(createOrderRequest)).thenReturn(Mono.error(Exception("Order creation failed")))

        webTestClient.post().uri("/order/checkout")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(objectMapper.writeValueAsString(createOrderRequest))
            .exchange()
            .expectStatus().isBadRequest
            .expectBody()
            .jsonPath("$.error").isEqualTo("Order creation failed")
    }

    @Test
    fun test003GetAllOrdersShouldReturn200AndListOfOrders() {
        val orders =
            listOf(
                Order(direction = "Order 1"),
                Order(direction = "Order 2"),
            )

        whenever(orderService.getAllOrders()).thenReturn(orders)

        webTestClient.get().uri("/order/getAll")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$[0].direction").isEqualTo("Order 1")
            .jsonPath("$[1].direction").isEqualTo("Order 2")
    }

    @Test
    fun test004GetAllOrdersShouldReturn400WhenServiceThrowsException() {
        whenever(orderService.getAllOrders()).thenThrow(RuntimeException("Service failure"))

        webTestClient.get().uri("/order/getAll")
            .exchange()
            .expectStatus().isBadRequest
            .expectBody()
    }
}
