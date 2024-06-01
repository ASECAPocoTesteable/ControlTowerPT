package com.controltowerpt.controllers.dto.request

data class CreateOrderRequest(
    val products: List<ProductQuantity>,
    val direction: String,
)

data class ProductQuantity(
    val productId: Long,
    val quantity: Int,
)
