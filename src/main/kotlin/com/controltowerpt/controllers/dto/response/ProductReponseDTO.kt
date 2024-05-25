package com.controltowerpt.controllers.dto.response

data class ProductResDTO(
    val name: String,
    val price: Double,
    val shopId: Long,
)

data class SaveProductRequest(
    val name: String,
    val price: Double,
    val shopId: Long,
)
