package com.controltowerpt.controllers

import com.controltowerpt.controllers.dto.request.SaveProductDTO
import com.controltowerpt.controllers.dto.request.UpdateProductPrice
import com.controltowerpt.controllers.dto.response.ProductResDTO
import com.controltowerpt.services.ProductService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/shop")
class AdminController(private val productService: ProductService) {
    @GetMapping("")
    fun getShop(): ResponseEntity<*> {
        return try {
            val shop = productService.getAllProducts()
            val productList =
                shop.map { product ->
                    ProductResDTO(
                        id = product.id,
                        name = product.name,
                        price = product.price,
                    )
                }
            ResponseEntity.ok(productList)
        } catch (e: Exception) {
            return ResponseEntity.badRequest().body(e.message)
        }
    }

    @PostMapping("/product/add")
    fun createProduct(
        @RequestBody req: SaveProductDTO,
    ): Mono<ResponseEntity<ProductResDTO>> {
        return productService.createProduct(req.name, req.price)
            .map { product ->
                ResponseEntity.ok(
                    ProductResDTO(
                        id = product.id,
                        name = product.name,
                        price = product.price,
                    ),
                )
            }
            .onErrorReturn(
                ResponseEntity.badRequest().body(
                    ProductResDTO(
                        id = -1,
                        name = "Error",
                        price = 0.0,
                    ),
                ),
            )
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
