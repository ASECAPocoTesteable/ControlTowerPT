package com.controltowerpt.servicesImpl

import com.controltowerpt.controllers.dto.request.CreateOrderRequest
import com.controltowerpt.models.Order
import com.controltowerpt.models.manytomany.ProductOrder
import com.controltowerpt.repositories.OrderRepository
import com.controltowerpt.repositories.ProductRepository
import com.controltowerpt.services.OrderService
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.util.retry.Retry
import java.time.Duration

@Service
class OrderServiceImpl(
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository,
    private val warehouseService: WarehouseService
) : OrderService {

    override fun createOrder(orderCreateDTO: CreateOrderRequest): Mono<Order> {
        if (orderCreateDTO.direction.isEmpty()) {
            return Mono.error(IllegalArgumentException("Direction cannot be empty"))
        }
        if (orderCreateDTO.products.isEmpty()) {
            return Mono.error(IllegalArgumentException("Products cannot be empty"))
        }

        // Pre-validation of product details
        orderCreateDTO.products.forEach { productQuantity ->
            if (productQuantity.productId < 1) {
                return Mono.error(IllegalArgumentException("Product id must be greater than 0"))
            }
            if (productQuantity.quantity < 1) {
                return Mono.error(IllegalArgumentException("Product quantity must be greater than 0"))
            }
        }

        // Check stock using WarehouseService with retry logic
        return warehouseService.checkStock(orderCreateDTO.products)
            .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)))  // Retry 3 times with a backoff of 1 second
            .flatMap { isStockSufficient ->
                if (!isStockSufficient) {
                    return@flatMap Mono.error<Order>(Exception("Stock is not sufficient"))
                }

                // Defer the blocking call to save the order
                Mono.defer {
                    val order = Order(direction = orderCreateDTO.direction)
                    orderCreateDTO.products.forEach { productQuantity ->
                        val productFound =
                            productRepository.findById(productQuantity.productId).orElseThrow {
                                IllegalArgumentException("Product with id ${productQuantity.productId} not found")
                            }
                        val productOrder = ProductOrder(productFound, order, productQuantity.quantity)
                        order.productOrders.add(productOrder)
                    }
                    Mono.fromCallable { orderRepository.save(order) }
                        .subscribeOn(Schedulers.boundedElastic()) // Ensure blocking calls are done on a separate thread
                }
            }
            .onErrorResume { throwable ->
                // Handle errors, such as warehouse service being down
                Mono.error(Exception("Failed to create order due to: ${throwable.message}", throwable))
            }
    }
}