package com.controltowerpt.controllers.dto.request

data class SaveShopRequest(
    val shopName: String,
)

data class GetByShopIDRequest(
    val shopID: Long,
)
