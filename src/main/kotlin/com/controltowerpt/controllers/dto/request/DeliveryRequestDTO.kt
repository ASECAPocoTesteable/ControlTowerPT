package com.controltowerpt.controllers.dto.request

data class NewDeliveryData(
    val orderId: Long,
    val warehouseDirection: String,
    val products: List<ProductQuantity>,
    val customerDirection: String,
)

data class DeliveryRequestDTO(
    val productId: Long,
    val quantity: Int,
)
