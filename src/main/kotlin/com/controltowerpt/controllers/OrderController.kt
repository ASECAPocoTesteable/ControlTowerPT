package com.controltowerpt.controllers

import com.controltowerpt.controllers.dto.request.CreateOrderRequest
import com.controltowerpt.controllers.dto.response.OrderInfoDTO
import com.controltowerpt.services.OrderService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/order")
class OrderController(private val orderService: OrderService) {
    @PostMapping("/checkout")
    fun checkoutCart(
        @RequestBody req: CreateOrderRequest,
    ): Mono<ResponseEntity<Any>> {
        return orderService.createOrder(req)
            .map { order ->
                val orderInfoDTO = OrderInfoDTO().fromOrder(order)
                ResponseEntity.ok(orderInfoDTO) as ResponseEntity<Any>
            }
            .onErrorResume { e ->
                Mono.just(ResponseEntity.badRequest().body(mapOf("error" to e.message)))
            }
    }

    @GetMapping("/getAll")
    fun getAllOrders(): ResponseEntity<Any> {
        return try {
            val orders = orderService.getAllOrders()
            ResponseEntity.ok(orders)
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(e.message)
        }
    }

    @DeleteMapping("/{orderId}")
    fun deleteOrder(
        @PathVariable orderId: Long,
    ): Mono<ResponseEntity<Any>> {
        return orderService.deleteOrder(orderId)
            .then(Mono.fromCallable { ResponseEntity.noContent().build<Any>() })
            .onErrorResume { e -> Mono.just(ResponseEntity.badRequest().body(mapOf("error" to e.message))) }
    }
}
