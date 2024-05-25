package com.controltowerpt.controllers

import com.controltowerpt.controllers.dto.request.SaveProductDTO
import com.controltowerpt.controllers.dto.response.ProductResDTO
import com.controltowerpt.services.ProductService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/product")
class ProductController(private val productService: ProductService) {
    @PostMapping("/add")
    fun createProduct(
        @RequestBody req: SaveProductDTO,
    ): ResponseEntity<*> {
        return try {
            val product = productService.createProduct(req.name, req.price, req.shopId)
            ResponseEntity.ok(
                ProductResDTO(
                    name = product.name,
                    price = product.price,
                    shopId = product.shop.id ?: throw IllegalArgumentException("Shop ID is missing"),
                ),
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(e.message)
        }
    }

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
                    name = productResult.name,
                    price = productResult.price,
                    shopId = productResult.shop.id ?: throw IllegalArgumentException("Shop ID is missing"),
                ),
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(e.message)
        }
    }

    @GetMapping("/getByShopId")
    fun getProductsByShopId(
        @RequestParam("shop") req: Long,
    ): ResponseEntity<*> {
        return try {
            val products = productService.findAllByShopId(req)
            ResponseEntity.ok(
                products.map {
                    ProductResDTO(
                        name = it.name,
                        price = it.price,
                        shopId = it.shop.id ?: throw IllegalArgumentException("Shop ID is missing"),
                    )
                },
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
                        name = it.name,
                        price = it.price,
                        shopId = it.shop.id ?: throw IllegalArgumentException("Shop ID is missing"),
                    )
                },
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(e.message)
        }
    }
}
