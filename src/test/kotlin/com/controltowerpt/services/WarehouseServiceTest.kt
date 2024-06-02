package com.controltowerpt.services

import com.controltowerpt.controllers.dto.request.ProductQuantity
import com.controltowerpt.models.Warehouse
import com.controltowerpt.repositories.WarehouseRepository
import com.controltowerpt.servicesImpl.WarehouseService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.util.*

class WarehouseServiceTest {
    private lateinit var webClient: WebClient
    private lateinit var warehouseRepository: WarehouseRepository
    private lateinit var warehouseService: WarehouseService

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
        val responseSpecMock = mock(WebClient.ResponseSpec::class.java)

        whenever(webClient.post()).thenReturn(requestBodyUriSpecMock)
        whenever(requestBodyUriSpecMock.uri(any<String>())).thenReturn(requestBodySpecMock)
        whenever(requestBodySpecMock.bodyValue(any())).thenReturn(requestHeadersSpecMock)
        whenever(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock)
        whenever(responseSpecMock.bodyToMono(Boolean::class.java)).thenReturn(Mono.just(true))

        val result = warehouseService.checkStock(products)

        StepVerifier.create(result)
            .expectNext(true)
            .verifyComplete()
    }

    @Test
    fun test002CheckStockFailure() {
        val products = listOf(ProductQuantity(productId = 1L, quantity = 2))

        val requestBodyUriSpecMock = mock(WebClient.RequestBodyUriSpec::class.java)
        val requestBodySpecMock = mock(WebClient.RequestBodySpec::class.java)
        val requestHeadersSpecMock = mock(WebClient.RequestHeadersSpec::class.java)
        val responseSpecMock = mock(WebClient.ResponseSpec::class.java)

        whenever(webClient.post()).thenReturn(requestBodyUriSpecMock)
        whenever(requestBodyUriSpecMock.uri(any<String>())).thenReturn(requestBodySpecMock)
        whenever(requestBodySpecMock.bodyValue(any())).thenReturn(requestHeadersSpecMock)
        whenever(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock)
        whenever(responseSpecMock.bodyToMono(Boolean::class.java)).thenReturn(Mono.error(RuntimeException("Internal Server Error")))

        val result = warehouseService.checkStock(products)

        StepVerifier.create(result)
            .expectNext(false)
            .verifyComplete()
    }

    @Test
    fun test003GetWarehouseByIDSuccess() {
        val warehouse = Warehouse().apply { id = 1L }
        whenever(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse))

        val result = warehouseService.getWarehouseByID(1L)

        assert(result == warehouse)
    }

    @Test
    fun test004GetWarehouseByIDFailure() {
        whenever(warehouseRepository.findById(1L)).thenReturn(Optional.empty())

        val exception =
            org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException::class.java) {
                warehouseService.getWarehouseByID(1L)
            }

        assert(exception.message == "Warehouse with id 1 not found")
    }

    @Test
    fun test005OrderHasBeenPickedUpSuccess() {
        val orderId = 1L

        val requestBodyUriSpecMock = mock(WebClient.RequestBodyUriSpec::class.java)
        val requestBodySpecMock = mock(WebClient.RequestBodySpec::class.java)
        val requestHeadersSpecMock = mock(WebClient.RequestHeadersSpec::class.java)
        val responseSpecMock = mock(WebClient.ResponseSpec::class.java)

        whenever(webClient.put()).thenReturn(requestBodyUriSpecMock)
        whenever(requestBodyUriSpecMock.uri(any<String>())).thenReturn(requestBodySpecMock)
        whenever(requestBodySpecMock.bodyValue(any())).thenReturn(requestHeadersSpecMock)
        whenever(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock)
        whenever(responseSpecMock.bodyToMono(Boolean::class.java)).thenReturn(Mono.just(true))

        val result = warehouseService.orderHasBeenPickedUp(orderId)

        StepVerifier.create(result)
            .expectNext(true)
            .verifyComplete()
    }

    @Test
    fun test006OrderHasBeenPickedUpFailure() {
        val orderId = 1L

        val requestBodyUriSpecMock = mock(WebClient.RequestBodyUriSpec::class.java)
        val requestBodySpecMock = mock(WebClient.RequestBodySpec::class.java)
        val requestHeadersSpecMock = mock(WebClient.RequestHeadersSpec::class.java)
        val responseSpecMock = mock(WebClient.ResponseSpec::class.java)

        whenever(webClient.put()).thenReturn(requestBodyUriSpecMock)
        whenever(requestBodyUriSpecMock.uri(any<String>())).thenReturn(requestBodySpecMock)
        whenever(requestBodySpecMock.bodyValue(any())).thenReturn(requestHeadersSpecMock)
        whenever(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock)
        whenever(responseSpecMock.bodyToMono(Boolean::class.java)).thenReturn(Mono.error(RuntimeException("Internal Server Error")))

        val result = warehouseService.orderHasBeenPickedUp(orderId)

        StepVerifier.create(result)
            .expectNext(false)
            .verifyComplete()
    }
}
