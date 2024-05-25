package com.controltowerpt.services

import com.controltowerpt.models.Product
import java.util.Optional

interface ProductService {
    fun createProduct(name : String, price : Double, shopId : Long) : Product

    fun findProductById(id : Long) : Optional<Product>

    fun findAllByShopId(shopId : Long) : List<Product>

    fun updateProductByPrice(newPrice: Double, id: Long): Product
}