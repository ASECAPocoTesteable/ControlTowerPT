package com.controltowerpt.controllers.dto.request

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class NewDeliveryData(
    val orderId: Long,
    val warehouseDirection: String,
    val products: List<ProductQuantityDTO>,
    val customerDirection: String,
)

data class DeliveryRequestDTO(
    val productId: Long,
    val quantity: Int,
)

data class OrderDTO
    @JsonCreator
    constructor(
        @JsonProperty("orderId") val orderId: Long,
        @JsonProperty("userAddress") val userAddress: String,
        @JsonProperty("products") val products: List<ProductQuantityDTO>,
        @JsonProperty("warehouseDirection") val warehouseDirection: String,
    )

data class ProductQuantityDTO
    @JsonCreator
    constructor(
        @JsonProperty("product") val product: String,
        @JsonProperty("quantity") val quantity: Int,
    )
