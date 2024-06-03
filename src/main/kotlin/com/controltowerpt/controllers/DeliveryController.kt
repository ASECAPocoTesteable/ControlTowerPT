package com.controltowerpt.controllers

import com.controltowerpt.services.OrderService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/delivery")
class DeliveryController(private val orderService: OrderService) {
    @PutMapping("/completed")
    fun deliveryCompleted(
        @RequestParam orderId: Long,
    ): ResponseEntity<*> {
        return try {
            orderService.orderDelivered(orderId)
            ResponseEntity.ok("success")
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(e.message)
        }
    }

    @PutMapping("/failed")
    fun deliveryFailed(
        @RequestParam orderId: Long,
    ): ResponseEntity<*> {
        return try {
            orderService.orderFailed(orderId)
            ResponseEntity.ok("success")
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(e.message)
        }
    }

    @PutMapping("/picked")
    fun deliveryPicked(
        @RequestParam orderId: Long,
    ): Mono<ResponseEntity<String>> {
        return orderService.orderHasBeenPicked(orderId)
            .map { success ->
                if (success) {
                    ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("Order has been picked successfully.")
                } else {
                    ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("Failed to update the order status.")
                }
            }
            .onErrorResume { e ->
                Mono.just(
                    ResponseEntity.badRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("Error: ${e.message}"),
                )
            }
    }
}
