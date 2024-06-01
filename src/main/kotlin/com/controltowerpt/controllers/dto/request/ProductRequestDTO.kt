package com.controltowerpt.controllers.dto.request

data class SaveProductDTO(
    val name: String,
    val price: Double,
    val shopId: Long,
)

data class UpdateProductPriceDTO(
    val newPrice: Double,
)
