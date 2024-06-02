package com.controltowerpt.servicesImpl

import com.controltowerpt.controllers.dto.request.NewDeliveryData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Service
class DeliveryService(
    @Autowired private val webClient: WebClient,
) {
    fun initializeDelivery(deliveryData: NewDeliveryData): Mono<Boolean> {
        val url = "http://localhost:8082/delivery/order/ready"

        return webClient.post()
            .uri(url)
            .bodyValue(deliveryData)
            .retrieve()
            .bodyToMono(Boolean::class.java)
            .onErrorResume { Mono.just(false) }
    }
}
