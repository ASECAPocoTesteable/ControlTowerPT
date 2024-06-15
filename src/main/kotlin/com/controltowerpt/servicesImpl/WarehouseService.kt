package com.controltowerpt.servicesImpl

import ProductStock
import ProductStockRequestDto
import com.controltowerpt.controllers.dto.request.ProductQuantity
import com.controltowerpt.controllers.dto.request.ProductWarehouseDTO
import com.controltowerpt.models.Warehouse
import com.controltowerpt.repositories.WarehouseRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Service
class WarehouseService(
    @Autowired private val webClient: WebClient,
    private val warehouseRepository: WarehouseRepository,
    @Autowired private val environment: Environment,
) {
    fun checkStock(products: List<ProductQuantity>): Mono<Boolean> {
        val url = "http://${environment.getProperty("warehouse_url")}/order/create"

        // Wrap the list in a ProductStockRequestDto
        val requestDto =
            ProductStockRequestDto(
                productList = products.map { ProductStock(it.productId, it.quantity) },
            )

        return webClient.post()
            .uri(url)
            .bodyValue(requestDto)
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

    fun orderHasBeenPickedUp(orderId: Long): Mono<String> {
        val url = "http://warehouseapi:8081/order/picked-up/$orderId"

        return webClient.put()
            .uri(url)
            .bodyValue("")
            .exchangeToMono { response ->
                if (response.statusCode().is2xxSuccessful) {
                    response.bodyToMono(String::class.java)
                } else {
                    response.bodyToMono(String::class.java).flatMap { errorBody ->
                        Mono.error<String>(Exception("Failed to mark order as picked up: $errorBody"))
                    }
                }
            }
    }

    fun createProduct(
        id: Long,
        name: String,
        stockQuantity: Int,
    ): Mono<String> {
        val url = "http://warehouseapi:8081/product/add"

        return webClient.post()
            .uri(url)
            .bodyValue(ProductWarehouseDTO(id, name, stockQuantity))
            .exchangeToMono { response ->
                if (response.statusCode().is2xxSuccessful) {
                    response.bodyToMono(String::class.java)
                } else {
                    response.bodyToMono(String::class.java).flatMap { errorBody ->
                        Mono.error<String>(Exception("Failed to create product: $errorBody"))
                    }
                }
            }
    }

    fun deleteOrder(orderId: Long): Mono<Void> {
        val url = "http://warehouseapi:8081/order/orders/$orderId"
        return webClient.delete()
            .uri(url)
            .retrieve()
            .bodyToMono(Void::class.java)
    }
}
