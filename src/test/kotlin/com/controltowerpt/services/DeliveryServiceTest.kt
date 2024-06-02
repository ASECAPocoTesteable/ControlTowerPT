import com.controltowerpt.controllers.dto.request.NewDeliveryData
import com.controltowerpt.controllers.dto.request.ProductQuantity
import com.controltowerpt.servicesImpl.DeliveryService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

class DeliveryServiceTest {
    private lateinit var webClient: WebClient
    private lateinit var deliveryService: DeliveryService

    @BeforeEach
    fun setUp() {
        webClient = mock(WebClient::class.java)
        deliveryService = DeliveryService(webClient)
    }

    @Test
    fun test001InitializeDeliverySuccess() {
        val deliveryData =
            NewDeliveryData(
                orderId = 1L,
                warehouseDirection = "Warehouse Direction",
                products = listOf(ProductQuantity(productId = 1L, quantity = 2)),
                customerDirection = "Customer Direction",
            )

        val requestBodyUriSpecMock = mock(WebClient.RequestBodyUriSpec::class.java)
        val requestBodySpecMock = mock(WebClient.RequestBodySpec::class.java)
        val requestHeadersSpecMock = mock(WebClient.RequestHeadersSpec::class.java)
        val responseSpecMock = mock(WebClient.ResponseSpec::class.java)

        whenever(webClient.post()).thenReturn(requestBodyUriSpecMock)
        whenever(requestBodyUriSpecMock.uri(any<String>())).thenReturn(requestBodySpecMock)
        whenever(requestBodySpecMock.bodyValue(any())).thenReturn(requestHeadersSpecMock)
        whenever(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock)
        whenever(responseSpecMock.bodyToMono(Boolean::class.java)).thenReturn(Mono.just(true))

        val result = deliveryService.initializeDelivery(deliveryData)

        StepVerifier.create(result)
            .expectNext(true)
            .verifyComplete()
    }

    @Test
    fun test002InitializeDeliveryFailure() {
        val deliveryData =
            NewDeliveryData(
                orderId = 1L,
                warehouseDirection = "Warehouse Direction",
                products = listOf(ProductQuantity(productId = 1L, quantity = 2)),
                customerDirection = "Customer Direction",
            )

        val requestBodyUriSpecMock = mock(WebClient.RequestBodyUriSpec::class.java)
        val requestBodySpecMock = mock(WebClient.RequestBodySpec::class.java)
        val requestHeadersSpecMock = mock(WebClient.RequestHeadersSpec::class.java)
        val responseSpecMock = mock(WebClient.ResponseSpec::class.java)

        whenever(webClient.post()).thenReturn(requestBodyUriSpecMock)
        whenever(requestBodyUriSpecMock.uri(any<String>())).thenReturn(requestBodySpecMock)
        whenever(requestBodySpecMock.bodyValue(any())).thenReturn(requestHeadersSpecMock)
        whenever(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock)
        whenever(responseSpecMock.bodyToMono(Boolean::class.java)).thenReturn(Mono.error(RuntimeException("Internal Server Error")))

        val result = deliveryService.initializeDelivery(deliveryData)

        StepVerifier.create(result)
            .expectNext(false)
            .verifyComplete()
    }
}
