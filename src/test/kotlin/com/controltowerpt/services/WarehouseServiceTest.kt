package com.controltowerpt.services

import com.controltowerpt.controllers.dto.request.ProductQuantity
import com.controltowerpt.models.Warehouse
import com.controltowerpt.repositories.WarehouseRepository
import com.controltowerpt.servicesImpl.WarehouseService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.util.*

class WarehouseServiceTest {
    private lateinit var webClient: WebClient
    private lateinit var warehouseService: WarehouseService
    private lateinit var warehouseRepository: WarehouseRepository

    @BeforeEach
    fun setUp() {
        webClient = mock(WebClient::class.java)
        warehouseRepository = mock(WarehouseRepository::class.java)
        warehouseService = WarehouseService(webClient, warehouseRepository)
    }

    @Test
    fun test001CheckStockSuccess() {
        val products = listOf(ProductQuantity(productId = 1L, quantity = 2))

        val requestBodyUriSpecMock = mock(WebClient.RequestBodyUriSpec::class.java)
        val requestBodySpecMock = mock(WebClient.RequestBodySpec::class.java)
        val requestHeadersSpecMock = mock(WebClient.RequestHeadersSpec::class.java)
        val clientResponseMock = mock(ClientResponse::class.java)

        whenever(webClient.post()).thenReturn(requestBodyUriSpecMock)
        whenever(requestBodyUriSpecMock.uri(any<String>())).thenReturn(requestBodySpecMock)
        whenever(requestBodySpecMock.bodyValue(any())).thenReturn(requestHeadersSpecMock)
        whenever(requestHeadersSpecMock.exchangeToMono<ClientResponse>(any())).thenReturn(Mono.just(clientResponseMock))
        whenever(clientResponseMock.statusCode()).thenReturn(HttpStatus.OK)
        whenever(clientResponseMock.bodyToMono(Boolean::class.java)).thenReturn(Mono.just(true))

        val result = warehouseService.checkStock(products)

        StepVerifier.create(result)
            .expectNext(true)
    }

    @Test
    fun test002CheckStockFailure() {
        val products = listOf(ProductQuantity(productId = 1L, quantity = 2))

        val requestBodyUriSpecMock = mock(WebClient.RequestBodyUriSpec::class.java)
        val requestBodySpecMock = mock(WebClient.RequestBodySpec::class.java)
        val requestHeadersSpecMock = mock(WebClient.RequestHeadersSpec::class.java)
        val clientResponseMock = mock(ClientResponse::class.java)

        whenever(webClient.post()).thenReturn(requestBodyUriSpecMock)
        whenever(requestBodyUriSpecMock.uri(any<String>())).thenReturn(requestBodySpecMock)
        whenever(requestBodySpecMock.bodyValue(any())).thenReturn(requestHeadersSpecMock)
        whenever(requestHeadersSpecMock.exchangeToMono<ClientResponse>(any())).thenReturn(Mono.just(clientResponseMock))
        whenever(clientResponseMock.statusCode()).thenReturn(HttpStatus.BAD_REQUEST)
        whenever(clientResponseMock.bodyToMono(String::class.java)).thenReturn(Mono.just("Bad Request"))

        val result = warehouseService.checkStock(products)

        StepVerifier.create(result)
            .expectErrorMatches { it.message == "Failed to check stock: Bad Request" }
    }

    @Test
    fun test003GetWarehouseByIDSuccess() {
        val warehouse =
            Warehouse().apply {
                id = 1L
                direction = "Some direction" // Ensure the direction field is set
            }

        whenever(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse))

        val result = warehouseService.getWarehouseByID(1L)

        assertEquals(warehouse.direction, result.direction)
    }

    @Test
    fun test004GetWarehouseByIDFailure() {
        whenever(warehouseRepository.findById(1L)).thenReturn(Optional.empty())

        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                warehouseService.getWarehouseByID(1L)
            }

        assertEquals("Warehouse with id 1 not found", exception.message)
    }

    @Test
    fun test005OrderHasBeenPickedUpSuccess() {
        val orderId = 1L

        val requestBodyUriSpecMock = mock(WebClient.RequestBodyUriSpec::class.java)
        val requestBodySpecMock = mock(WebClient.RequestBodySpec::class.java)
        val requestHeadersSpecMock = mock(WebClient.RequestHeadersSpec::class.java)
        val clientResponseMock = mock(ClientResponse::class.java)

        whenever(webClient.put()).thenReturn(requestBodyUriSpecMock)
        whenever(requestBodyUriSpecMock.uri(any<String>())).thenReturn(requestBodySpecMock)
        whenever(requestBodySpecMock.bodyValue(any())).thenReturn(requestHeadersSpecMock)
        whenever(requestHeadersSpecMock.exchangeToMono<ClientResponse>(any())).thenReturn(Mono.just(clientResponseMock))
        whenever(clientResponseMock.statusCode()).thenReturn(HttpStatus.OK)
        whenever(clientResponseMock.bodyToMono(Boolean::class.java)).thenReturn(Mono.just(true))

        val result = warehouseService.orderHasBeenPickedUp(orderId)

        StepVerifier.create(result)
            .expectNext(true)
    }

    @Test
    fun test006OrderHasBeenPickedUpFailure() {
        val orderId = 1L

        val requestBodyUriSpecMock = mock(WebClient.RequestBodyUriSpec::class.java)
        val requestBodySpecMock = mock(WebClient.RequestBodySpec::class.java)
        val requestHeadersSpecMock = mock(WebClient.RequestHeadersSpec::class.java)
        val clientResponseMock = mock(ClientResponse::class.java)

        whenever(webClient.put()).thenReturn(requestBodyUriSpecMock)
        whenever(requestBodyUriSpecMock.uri(any<String>())).thenReturn(requestBodySpecMock)
        whenever(requestBodySpecMock.bodyValue(any())).thenReturn(requestHeadersSpecMock)
        whenever(requestHeadersSpecMock.exchangeToMono<ClientResponse>(any())).thenReturn(Mono.just(clientResponseMock))
        whenever(clientResponseMock.statusCode()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR)
        whenever(clientResponseMock.bodyToMono(String::class.java)).thenReturn(Mono.just("Internal Server Error"))

        val result = warehouseService.orderHasBeenPickedUp(orderId)

        StepVerifier.create(result)
            .expectErrorMatches { it is Exception && it.message?.contains("Failed to notify warehouse: Internal Server Error") == true }
    }
}
