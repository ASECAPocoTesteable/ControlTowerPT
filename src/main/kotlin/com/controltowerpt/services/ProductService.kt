package com.controltowerpt.services

import com.controltowerpt.models.Product
import reactor.core.publisher.Mono
import java.util.Optional

interface ProductService {
    fun createProduct(
        name: String,
        price: Double,
    ): Mono<Product>

    fun findProductById(id: Long): Optional<Product>

    fun updateProductByPrice(
        newPrice: Double,
        id: Long,
    ): Product

    fun getAllProducts(): List<Product>

    fun deleteProduct(id: Long)
}
