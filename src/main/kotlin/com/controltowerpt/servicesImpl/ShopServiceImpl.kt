package com.controltowerpt.servicesImpl

import com.controltowerpt.models.Shop
import com.controltowerpt.repositories.ShopRepository
import com.controltowerpt.services.ShopService
import jakarta.persistence.*
import org.springframework.stereotype.Service

@Service
class ShopServiceImpl(private val shopRepository: ShopRepository) : ShopService {
    override fun createShop(shop: Shop): Shop {
        if (shop.name.isEmpty()) {
            throw IllegalArgumentException("Shop name cannot be empty")
        }
        return shopRepository.save(shop)
    }

    override fun findShopById(id: Long): Shop {
        if (id < 1) {
            throw IllegalArgumentException("Shop id must be greater than 0")
        }

        val shop = shopRepository.findById(id).orElse(null) ?: throw EntityNotFoundException("Shop not found")

        return shop
    }

    override fun deleteShopById(id: Long) {
        shopRepository.delete(findShopById(id))
    }
}
