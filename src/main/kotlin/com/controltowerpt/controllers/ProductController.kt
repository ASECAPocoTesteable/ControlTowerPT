package com.controltowerpt.controllers

import com.controltowerpt.controllers.dto.response.ProductResDTO
import com.controltowerpt.services.ProductService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/product")
class ProductController(private val productService: ProductService) {
    @GetMapping("/getById")
    fun getProductById(
        @RequestParam("id") req: Long,
    ): ResponseEntity<*> {
        return try {
            val product = productService.findProductById(req)
            if (product.isEmpty) {
                throw IllegalArgumentException("Product not found")
            }
            val productResult = product.get()
            ResponseEntity.ok(
                ProductResDTO(
                    id = productResult.id,
                    name = productResult.name,
                    price = productResult.price,
                ),
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(e.message)
        }
    }

    @GetMapping("/getAll")
    fun getAllProducts(): ResponseEntity<*> {
        return try {
            val products = productService.getAllProducts()
            ResponseEntity.ok(
                products.map {
                    ProductResDTO(
                        id = it.id,
                        name = it.name,
                        price = it.price,
                    )
                },
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(e.message)
        }
    }
}
