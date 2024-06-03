package com.controltowerpt.controllers

import com.controltowerpt.controllers.dto.request.SaveProductDTO
import com.controltowerpt.controllers.dto.request.UpdateProductPrice
import com.controltowerpt.controllers.dto.response.ProductResDTO
import com.controltowerpt.services.ProductService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/shop")
class AdminController(private val productService: ProductService) {
    @GetMapping("")
    fun getShop(): ResponseEntity<*> {
        return try {
            ResponseEntity.ok("Esto es una prueba de update")
        } catch (e: Exception) {
            return ResponseEntity.badRequest().body(e.message)
        }
    }

    @PostMapping("/product/add")
    fun createProduct(
        @RequestBody req: SaveProductDTO,
    ): ResponseEntity<*> {
        return try {
            val product = productService.createProduct(req.name, req.price)
            ResponseEntity.ok(
                ProductResDTO(
                    id = product.id,
                    name = product.name,
                    price = product.price,
                ),
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(e.message)
        }
    }

    @DeleteMapping("/delete/product")
    fun deleteProduct(
        @RequestParam id: Long,
    ): ResponseEntity<*> {
        return try {
            productService.deleteProduct(id)
            ResponseEntity.ok("Product deleted")
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(e.message)
        }
    }

    @PutMapping("/update/product/price")
    fun updateProductPrice(
        @RequestBody req: UpdateProductPrice,
    ): ResponseEntity<*> {
        return try {
            val product = productService.updateProductByPrice(req.price, req.id)
            ResponseEntity.ok(
                ProductResDTO(
                    id = product.id,
                    name = product.name,
                    price = product.price,
                ),
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(e.message)
        }
    }
}
