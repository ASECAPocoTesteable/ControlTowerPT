package com.controltowerpt.controllers

import com.controltowerpt.controllers.dto.request.SaveProductDTO
import com.controltowerpt.services.ProductService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/product")
class ProductController(private val productService : ProductService) {


    @PostMapping("/add")
    fun createProduct(@RequestBody req : SaveProductDTO) : ResponseEntity<*> {
        try {
            val product = productService.createProduct(req.name, req.price, req.shopId)
            return ResponseEntity.ok().body(product)
        } catch (e : Exception) {
            return ResponseEntity.badRequest().body(e.message)
        }
    }
}