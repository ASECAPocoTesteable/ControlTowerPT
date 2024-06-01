package com.controltowerpt.services

import com.controltowerpt.controllers.dto.request.CreateOrderRequest
import com.controltowerpt.models.Order
import reactor.core.publisher.Mono

interface OrderService {
    fun createOrder(orderCreateDTO: CreateOrderRequest): Mono<Order>

    fun getAllOrders(): List<Order>
}
