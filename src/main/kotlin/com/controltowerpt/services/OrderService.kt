package com.controltowerpt.services

import com.controltowerpt.controllers.dto.request.CreateOrderRequest
import com.controltowerpt.controllers.dto.response.OrderInfoDTO
import com.controltowerpt.models.Order
import reactor.core.publisher.Mono

interface OrderService {
    fun createOrder(orderCreateDTO: CreateOrderRequest): Mono<Order>

    fun getAllOrders(): List<OrderInfoDTO>

    fun orderIsReady(orderId: Long): Mono<Boolean>

    fun orderHasBeenPicked(orderId: Long): Mono<Boolean>

    fun orderDelivered(orderId: Long)

    fun orderFailed(orderId: Long)

    fun deleteOrder(orderId: Long): Mono<Void>
}
