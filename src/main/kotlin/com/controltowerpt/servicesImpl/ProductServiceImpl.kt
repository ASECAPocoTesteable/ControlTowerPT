package com.controltowerpt.servicesImpl

import com.controltowerpt.models.Product
import com.controltowerpt.repositories.ProductRepository
import com.controltowerpt.services.ProductService
import com.controltowerpt.services.ShopService
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class ProductServiceImpl(private val productRepository: ProductRepository, private val shopService: ShopService) : ProductService {
    override fun createProduct(
        name: String,
        price: Double,
        shopId: Long,
    ): Product {
        if (name.isEmpty()) {
            throw IllegalArgumentException("Product name cannot be empty")
        }
        if (price <= 0) {
            throw IllegalArgumentException("Product price must be greater than 0")
        }
        if (shopId < 1) {
            throw IllegalArgumentException("Shop id must be greater than 0")
        }

        val shop = shopService.findShopById(shopId) ?: throw IllegalArgumentException("Shop not found")
        val product = Product(name = name, price = price, shop = shop)
        return productRepository.save(product)
    }

    override fun findProductById(id: Long): Optional<Product> {
        if (id < 1) {
            throw IllegalArgumentException("Product id must be greater than 0")
        }

        return productRepository.findById(id)
    }

    override fun findAllByShopId(shopId: Long): List<Product> {
        if (shopId < 1) {
            throw IllegalArgumentException("Shop id must be greater than 0")
        }

        return productRepository.findByShopId(shopId)
    }

    override fun updateProductByPrice(
        newPrice: Double,
        id: Long,
    ): Product {
        if (id < 1) {
            throw IllegalArgumentException("Product id must be greater than 0")
        }
        if (newPrice <= 0) {
            throw IllegalArgumentException("Product price must be greater than 0")
        }
        productRepository.updateProductByPrice(newPrice, id)

        return productRepository.findById(id).orElseThrow { IllegalArgumentException("Product not found") }
    }

    override fun getAllProducts(): List<Product> {
        return productRepository.findAll()
    }
}
