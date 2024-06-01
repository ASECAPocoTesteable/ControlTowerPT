package com.controltowerpt.controllers.dto.response

data class ProductResDTO(
    val id: Long,
    val name: String,
    val price: Double,
)

data class SaveProductRequest(
    val name: String,
    val price: Double,
    val shopId: Long,
)
