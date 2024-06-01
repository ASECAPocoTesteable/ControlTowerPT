package com.controltowerpt.controllers

import com.controltowerpt.controllers.dto.request.CreateOrderRequest
import com.controltowerpt.services.OrderService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/order")
class OrderController(private val orderService: OrderService) {
    @GetMapping("/checkout")
    fun checkoutCart(req: CreateOrderRequest): ResponseEntity<*> {
        return try {
            val order = orderService.createOrder(req)
            ResponseEntity.ok(order)
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(e.message)
        }
    }
}
