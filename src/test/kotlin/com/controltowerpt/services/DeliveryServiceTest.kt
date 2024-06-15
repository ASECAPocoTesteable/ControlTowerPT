import com.controltowerpt.controllers.dto.request.NewDeliveryData
import com.controltowerpt.controllers.dto.request.ProductQuantityDTO
import com.controltowerpt.servicesImpl.DeliveryService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.whenever
import org.springframework.core.env.Environment
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

class DeliveryServiceTest {
    private lateinit var webClient: WebClient

    private lateinit var deliveryService: DeliveryService

    private lateinit var environment: Environment

    @BeforeEach
    fun setUp() {
        environment = mock(Environment::class.java)
        webClient = mock(WebClient::class.java)
        deliveryService = DeliveryService(webClient, environment)
    }

    @Test
    fun testInitializeDeliverySuccess() {
        val deliveryData =
            NewDeliveryData(
                orderId = 1L,
                warehouseDirection = "Warehouse Direction",
                products = listOf(ProductQuantityDTO(product = "caca", quantity = 2)),
                customerDirection = "Customer Direction",
            )

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

        val result = deliveryService.initializeDelivery(deliveryData)

        StepVerifier.create(result)
            .expectNext(true)
    }

    @Test
    fun testInitializeDeliveryFailure() {
        val deliveryData =
            NewDeliveryData(
                orderId = 1L,
                warehouseDirection = "Warehouse Direction",
                products = listOf(ProductQuantityDTO(product = "caca", quantity = 2)),
                customerDirection = "Customer Direction",
            )

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

        val result = deliveryService.initializeDelivery(deliveryData)

        StepVerifier.create(result)
            .expectErrorMatches { it.message == "Failed to initialize delivery: Bad Request" }
    }
}
