package com.controltowerpt.servicesImpl

import com.controltowerpt.controllers.dto.request.CreateOrderRequest
import com.controltowerpt.controllers.dto.request.NewDeliveryData
import com.controltowerpt.controllers.dto.request.ProductQuantity
import com.controltowerpt.models.Order
import com.controltowerpt.models.OrderState
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
    private val warehouseService: WarehouseService,
    private val deliveryService: DeliveryService,
) : OrderService {
    override fun createOrder(orderCreateDTO: CreateOrderRequest): Mono<Order> {
        if (orderCreateDTO.direction.isEmpty()) {
            return Mono.error(IllegalArgumentException("Direction cannot be empty"))
        }
        if (orderCreateDTO.products.isEmpty()) {
            return Mono.error(IllegalArgumentException("Products cannot be empty"))
        }

        orderCreateDTO.products.forEach { productQuantity ->
            if (productQuantity.productId < 1) {
                return Mono.error(IllegalArgumentException("Product id must be greater than 0"))
            }
            if (productQuantity.quantity < 1) {
                return Mono.error(IllegalArgumentException("Product quantity must be greater than 0"))
            }
        }

        return warehouseService.checkStock(orderCreateDTO.products)
            .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)))
            .flatMap { isStockSufficient ->
                if (!isStockSufficient) {
                    return@flatMap Mono.error<Order>(Exception("Stock is not sufficient"))
                }

                Mono.defer {
                    val warehouseEntity = warehouseService.getWarehouseByID(1L)
                    val order = Order(clientDirection = orderCreateDTO.direction).apply { warehouse = warehouseEntity }
                    orderCreateDTO.products.forEach { productQuantity ->
                        val productFound =
                            productRepository.findById(productQuantity.productId).orElseThrow {
                                IllegalArgumentException("Product with id ${productQuantity.productId} not found")
                            }
                        val productOrder = ProductOrder(productFound, order, productQuantity.quantity)
                        order.productOrders.add(productOrder)
                    }
                    Mono.fromCallable { orderRepository.save(order) }
                        .subscribeOn(Schedulers.boundedElastic())
                }
            }
            .onErrorResume { throwable ->
                Mono.error(Exception("Failed to create order due to: ${throwable.message}", throwable))
            }
    }

    override fun getAllOrders(): List<Order> {
        return orderRepository.findAll()
    }

    override fun orderIsReady(orderId: Long): Mono<Boolean> {
        if (orderId < 1) {
            return Mono.error(IllegalArgumentException("Order id must be greater than 0"))
        }

        return Mono.fromCallable {
            val order =
                orderRepository.findById(orderId).orElseThrow {
                    IllegalArgumentException("Order with id $orderId not found")
                }

            val newDeliveryData =
                NewDeliveryData(
                    orderId,
                    order.warehouse?.direction ?: throw IllegalArgumentException("Warehouse direction not found"),
                    order.productOrders.map { ProductQuantity(it.product.id, it.amount) },
                    order.clientDirection,
                )
            order to newDeliveryData
        }
            .subscribeOn(Schedulers.boundedElastic())
            .flatMap { (order, deliveryData) ->
                deliveryService.initializeDelivery(deliveryData)
                    .flatMap { success ->
                        if (success) {
                            order.state = OrderState.PREPARED
                            Mono.fromCallable { orderRepository.save(order) }
                                .subscribeOn(Schedulers.boundedElastic())
                                .thenReturn(true)
                        } else {
                            Mono.just(false)
                        }
                    }
            }
    }

    override fun orderHasBeenPicked(orderId: Long): Mono<Boolean> {
        if (orderId < 1) {
            return Mono.error(IllegalArgumentException("Order id must be greater than 0"))
        }

        return Mono.fromCallable {
            val order =
                orderRepository.findById(orderId).orElseThrow {
                    IllegalArgumentException("Order with id $orderId not found")
                }

            if (order.state != OrderState.PREPARED) {
                throw IllegalStateException("Order must be in prepared state to be picked")
            }

            order.state = OrderState.IN_DELIVERY
            orderRepository.save(order)
            order.id
        }
            .subscribeOn(Schedulers.boundedElastic())
            .flatMap { savedOrderId ->
                warehouseService.orderHasBeenPickedUp(savedOrderId)
                    .flatMap { pickedUpSuccess ->
                        if (pickedUpSuccess) {
                            Mono.just(true)
                        } else {
                            Mono.error(Exception("Failed to notify warehouse that the order has been picked up"))
                        }
                    }
            }
    }

    override fun orderDelivered(orderId: Long) {
        if (orderId < 1) {
            throw IllegalArgumentException("Order id must be greater than 0")
        }

        val order =
            orderRepository.findById(orderId).orElseThrow {
                IllegalArgumentException("Order with id $orderId not found")
            }

        if (order.state != OrderState.IN_DELIVERY) {
            throw IllegalStateException("Order must be in delivery state to be delivered")
        }

        order.state = OrderState.DELIVERED

        orderRepository.save(order)
    }


    override fun orderFailed(orderId: Long) {
        if (orderId < 1) {
            throw IllegalArgumentException("Order id must be greater than 0")
        }

        val order =
            orderRepository.findById(orderId).orElseThrow {
                IllegalArgumentException("Order with id $orderId not found")
            }

        if (order.state != OrderState.IN_DELIVERY) {
            throw IllegalStateException("Order must be in delivery state to fail")
        }

        order.state = OrderState.FAILED

        orderRepository.save(order)
    }
}
