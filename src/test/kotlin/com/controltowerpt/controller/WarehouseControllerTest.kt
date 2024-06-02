import com.controltowerpt.controllers.WarehouseController
import com.controltowerpt.services.OrderService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono

@WebFluxTest(WarehouseController::class)
@ContextConfiguration(classes = [WarehouseController::class])
class WarehouseControllerTest {
    @Autowired
    private lateinit var webTestClient: WebTestClient

    @MockBean
    private lateinit var orderService: OrderService

    @Test
    fun test001OrderReadySuccess() {
        whenever(orderService.orderIsReady(any())).thenReturn(Mono.just(true))

        webTestClient.put().uri("/warehouse/order/ready?orderId=1")
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$").isEqualTo("Delivery service was reached successfully.")
    }

    @Test
    fun test002OrderReadyFailure() {
        whenever(orderService.orderIsReady(any())).thenReturn(Mono.just(false))

        webTestClient.put().uri("/warehouse/order/ready?orderId=1")
            .exchange()
            .expectStatus().isEqualTo(503)
            .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$").isEqualTo("Failed to reach delivery service.")
    }

    @Test
    fun test003OrderReadyError() {
        whenever(orderService.orderIsReady(any())).thenReturn(Mono.error(RuntimeException("Order id must be greater than 0")))

        webTestClient.put().uri("/warehouse/order/ready?orderId=0")
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$").isEqualTo("Order id must be greater than 0")
    }
}
