package com.controltowerpt.servicesImpl

import com.controltowerpt.controllers.dto.request.ProductQuantity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Service
class WarehouseService(@Autowired private val webClient: WebClient) {

    fun checkStock(products: List<ProductQuantity>): Mono<Boolean> {
        val url = "http://localhost:8081/warehouse/stock/check"

        return webClient.post()
            .uri(url)
            .bodyValue(products)
            .retrieve()
            .bodyToMono(Boolean::class.java)
            .onErrorResume { Mono.just(false) }
    }
}
