package com.controltowerpt.services

import com.controltowerpt.models.Shop

interface ShopService {
    fun createShop(shop: Shop): Shop

    fun findShopById(id: Long): Shop?

    fun deleteShopById(id: Long)
}
