package com.controltowerpt.servicesImpl

import com.controltowerpt.controllers.dto.request.ProductQuantity
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
        val url = "http://localhost:8081/warehouse/stock/check"

        return webClient.post()
            .uri(url)
            .bodyValue(products)
            .retrieve()
            .bodyToMono(Boolean::class.java)
            .onErrorResume { Mono.just(false) }
    }

    fun getWarehouseByID(id: Long): Warehouse {
        return warehouseRepository.findById(id).orElseThrow {
            IllegalArgumentException("Warehouse with id $id not found")
        }
    }
}
