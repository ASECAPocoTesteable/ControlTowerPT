package com.controltowerpt.controllers

import ShopResponse
import com.controltowerpt.controllers.dto.request.SaveShopRequest
import com.controltowerpt.models.Shop
import com.controltowerpt.services.ShopService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/shop")
class ShopController(private val shopService: ShopService) {
    @PostMapping("/add")
    fun postSaveShop(@RequestBody shop: SaveShopRequest ): ResponseEntity<*> {
        return try {
            val shopCreated = shopService.createShop(Shop(shop.shopName))
            val res = ShopResponse(shopCreated.name)
            ResponseEntity.ok().body(res)
        } catch (e: IllegalArgumentException) {
            return ResponseEntity.badRequest().body(e.message)
        } catch (e: Exception) {
            return ResponseEntity.badRequest().body(e.message)
        }
    }


    @GetMapping("/get")
    fun getShop(@RequestParam id : Long): ResponseEntity<*> {
        return try {
            val shop = shopService.findShopById(id)
            val res = shop?.let { ShopResponse(it.name) }
            ResponseEntity.ok().body(res)
        } catch (e: Exception) {
            return ResponseEntity.badRequest().body(e.message)
        }
    }

}
