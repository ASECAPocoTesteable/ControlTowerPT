package com.controltowerpt.services

import com.controltowerpt.controllers.dto.request.CreateOrderRequest
import com.controltowerpt.models.Order

interface OrderService {
    fun createOrder(orderCreateDTO: CreateOrderRequest): Order
}
