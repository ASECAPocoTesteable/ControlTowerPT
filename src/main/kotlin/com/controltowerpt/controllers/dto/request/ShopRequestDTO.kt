package com.controltowerpt.controllers.dto.request

data class SaveShopRequest(
    val shopName: String,
)

data class UpdateProductPrice(
    val id: Long,
    val price: Double,
)
