package com.controltowerpt.servicesImpl

import com.controltowerpt.controllers.dto.request.NewDeliveryData
import com.controltowerpt.controllers.dto.request.OrderDTO
import com.controltowerpt.controllers.dto.request.ProductQuantityDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Service
class DeliveryService(
    @Autowired private val webClient: WebClient,
) {
    fun initializeDelivery(deliveryData: NewDeliveryData): Mono<Boolean> {
        val url = "http://deliveryapi:8082/order"

        val orderDTO = transformToOrderDTO(deliveryData)

        return webClient.post()
            .uri(url)
            .bodyValue(orderDTO)
            .exchangeToMono { response ->
                if (response.statusCode().is2xxSuccessful) {
                    response.bodyToMono(Boolean::class.java)
                } else {
                    response.bodyToMono(String::class.java).flatMap { errorBody ->
                        Mono.error<Boolean>(Exception("Failed to initialize delivery: $errorBody"))
                    }
                }
            }
    }

    fun transformToOrderDTO(newDeliveryData: NewDeliveryData): OrderDTO {
        val products =
            newDeliveryData.products.map {
                ProductQuantityDTO(
                    product = it.product, // Assuming product ID is used as the product name/identifier
                    quantity = it.quantity,
                )
            }

        return OrderDTO(
            orderId = newDeliveryData.orderId,
            userAddress = newDeliveryData.customerDirection,
            products = products,
            warehouseDirection = newDeliveryData.warehouseDirection,
        )
    }

    fun deleteOrder(orderId: Long): Mono<Void> {
        val url = "http://deliveryapi:8082/order/$orderId"
        return webClient.delete()
            .uri(url)
            .retrieve()
            .bodyToMono(Void::class.java)
    }
}
