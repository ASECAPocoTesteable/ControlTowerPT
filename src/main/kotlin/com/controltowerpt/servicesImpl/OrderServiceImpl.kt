package com.controltowerpt.servicesImpl

import com.controltowerpt.controllers.dto.request.CreateOrderRequest
import com.controltowerpt.models.Order
import com.controltowerpt.models.manytomany.ProductOrder
import com.controltowerpt.repositories.OrderRepository
import com.controltowerpt.repositories.ProductRepository
import com.controltowerpt.services.OrderService
import org.springframework.stereotype.Service

@Service
class OrderServiceImpl(private val orderRepository: OrderRepository, private val productRepository: ProductRepository) :
    OrderService {
    override fun createOrder(orderCreateDTO: CreateOrderRequest): Order {
        if (orderCreateDTO.direction.isEmpty()) {
            throw IllegalArgumentException("Direction cannot be empty")
        }
        if (orderCreateDTO.products.isEmpty()) {
            throw IllegalArgumentException("Products cannot be empty")
        }
        val order = Order(direction = orderCreateDTO.direction)
        orderCreateDTO.products.forEach {
            if (it.productId < 1) {
                throw IllegalArgumentException("Product id must be greater than 0")
            }
            if (it.quantity < 1) {
                throw IllegalArgumentException("Product quantity must be greater than 0")
            }
            val productFound =
                productRepository.findById(it.productId).orElseThrow {
                    IllegalArgumentException("Product with id ${it.productId} not found")
                }
            val productOrder = ProductOrder(productFound, order, it.quantity)
            order.productOrders.add(productOrder)
        }
        return orderRepository.save(order)
    }
}
