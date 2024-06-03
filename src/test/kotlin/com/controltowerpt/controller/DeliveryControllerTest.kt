import com.controltowerpt.controllers.DeliveryController
import com.controltowerpt.services.OrderService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono

@WebFluxTest(DeliveryController::class)
@ContextConfiguration(classes = [DeliveryController::class])
class DeliveryControllerTest {
    @Autowired
    private lateinit var webTestClient: WebTestClient

    @MockBean
    private lateinit var orderService: OrderService

    @Test
    fun test001DeliveryPickedSuccess() {
        whenever(orderService.orderHasBeenPicked(any())).thenReturn(Mono.just(true))

        webTestClient.put().uri("/delivery/picked?orderId=1")
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$").isEqualTo("Order has been picked successfully.")
    }

    @Test
    fun test002DeliveryPickedFailure() {
        whenever(orderService.orderHasBeenPicked(any())).thenReturn(Mono.just(false))

        webTestClient.put().uri("/delivery/picked?orderId=1")
            .exchange()
            .expectStatus().isEqualTo(503)
            .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$").isEqualTo("Failed to update the order status.")
    }

    @Test
    fun test003DeliveryPickedError() {
        whenever(orderService.orderHasBeenPicked(any())).thenReturn(Mono.error(RuntimeException("Order id must be greater than 0")))

        webTestClient.put().uri("/delivery/picked?orderId=0")
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$").isEqualTo("Error: Order id must be greater than 0")
    }

    @Test
    fun test004DeliveryCompletedSuccess() {
        doNothing().whenever(orderService).orderDelivered(any())

        webTestClient.put().uri("/delivery/completed?orderId=1")
            .exchange()
            .expectStatus().isOk
            .expectBody().isEmpty
    }

    @Test
    fun test005DeliveryCompletedError() {
        doThrow(IllegalArgumentException("Order not found")).whenever(orderService).orderDelivered(any())

        webTestClient.put().uri("/delivery/completed?orderId=1")
            .exchange()
            .expectStatus().isBadRequest
            .expectBody()
            .jsonPath("$").isEqualTo("Order not found")
    }

    @Test
    fun test006DeliveryFailedSuccess() {
        doNothing().whenever(orderService).orderFailed(any())

        webTestClient.put().uri("/delivery/failed?orderId=1")
            .exchange()
            .expectStatus().isOk
            .expectBody().isEmpty
    }

    @Test
    fun test007DeliveryFailedError() {
        doThrow(IllegalArgumentException("Order not found")).whenever(orderService).orderFailed(any())

        webTestClient.put().uri("/delivery/failed?orderId=1")
            .exchange()
            .expectStatus().isBadRequest
            .expectBody()
            .jsonPath("$").isEqualTo("Order not found")
    }
}
