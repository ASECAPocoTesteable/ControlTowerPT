package com.controltowerpt.servicesImpl

import com.controltowerpt.controllers.dto.request.ProductQuantity
import com.controltowerpt.controllers.dto.request.ProductWarehouseDTO
import com.controltowerpt.models.Warehouse
import com.controltowerpt.repositories.WarehouseRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Service
class WarehouseService(
    @Autowired private val webClient: WebClient,
    private val warehouseRepository: WarehouseRepository,
) {
    fun checkStock(products: List<ProductQuantity>): Mono<Boolean> {
        val url = "http://warehouseapi:8081/stock/check"

        return webClient.post()
            .uri(url)
            .bodyValue(products)
            .exchangeToMono { response ->
                if (response.statusCode().is2xxSuccessful) {
                    response.bodyToMono(Boolean::class.java)
                } else {
                    response.bodyToMono(String::class.java).flatMap { errorBody ->
                        Mono.error<Boolean>(Exception("Failed to check stock: $errorBody"))
                    }
                }
            }
    }

    fun getWarehouseByID(id: Long): Warehouse {
        return warehouseRepository.findById(id).orElseThrow {
            IllegalArgumentException("Warehouse with id $id not found")
        }
    }

    fun orderHasBeenPickedUp(orderId: Long): Mono<Boolean> {
        val url = "http://warehouseapi:8081/warehouse/order/picked-up"

        return webClient.put()
            .uri(url)
            .bodyValue(orderId)
            .exchangeToMono { response ->
                if (response.statusCode().is2xxSuccessful) {
                    response.bodyToMono(Boolean::class.java)
                } else {
                    response.bodyToMono(String::class.java).flatMap { errorBody ->
                        Mono.error<Boolean>(Exception("Failed to notify warehouse: $errorBody"))
                    }
                }
            }
    }

    fun createProduct(
        id: Long,
        name: String,
        stockQuantity: Int,
    ): Mono<Boolean> {
        val url = "http://warehouseapi:8081/product/add"

        return webClient.post()
            .uri(url)
            .bodyValue(ProductWarehouseDTO(id, name, stockQuantity))
            .exchangeToMono { response ->
                if (response.statusCode().is2xxSuccessful) {
                    response.bodyToMono(Boolean::class.java)
                } else {
                    response.bodyToMono(String::class.java).flatMap { errorBody ->
                        Mono.error<Boolean>(Exception("Failed to create product: $errorBody"))
                    }
                }
            }
    }
}
