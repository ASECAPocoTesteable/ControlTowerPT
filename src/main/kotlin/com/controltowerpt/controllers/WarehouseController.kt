package com.controltowerpt.controllers

import com.controltowerpt.services.OrderService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/warehouse")
class WarehouseController(private val orderService: OrderService) {
    @PutMapping("/order/ready")
    fun orderReady(
        @RequestParam orderId: Long,
    ): Mono<ResponseEntity<String>> {
        return orderService.orderIsReady(orderId)
            .map { success ->
                if (success) {
                    ResponseEntity.ok("Delivery service was reached successfully.")
                } else {
                    ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Failed to reach delivery service.")
                }
            }
            .onErrorResume { e ->
                Mono.just(ResponseEntity.badRequest().body(e.message))
            }
    }
}
