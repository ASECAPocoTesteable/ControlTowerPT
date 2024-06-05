package com.controltowerpt.servicesImpl

import com.controltowerpt.models.Product
import com.controltowerpt.repositories.ProductRepository
import com.controltowerpt.services.ProductService
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.util.Optional

@Service
class ProductServiceImpl(
    private val productRepository: ProductRepository,
    private val warehouseService: WarehouseService,
) : ProductService {
    override fun createProduct(
        name: String,
        price: Double,
    ): Mono<Product> {
        if (name.isEmpty()) {
            return Mono.error(IllegalArgumentException("Product name cannot be empty"))
        }
        if (price <= 0) {
            return Mono.error(IllegalArgumentException("Product price must be greater than 0"))
        }
        val product = Product(name = name, price = price)
        return warehouseService.createProduct(product.id, product.name, 0)
            .flatMap {
                Mono.fromCallable {
                    productRepository.save(product)
                }.subscribeOn(Schedulers.boundedElastic())
            }
            .map { product }
    }

    override fun findProductById(id: Long): Optional<Product> {
        if (id < 1) {
            throw IllegalArgumentException("Product id must be greater than 0")
        }

        return productRepository.findById(id)
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

    override fun deleteProduct(id: Long) {
        if (id < 1) {
            throw IllegalArgumentException("Product id must be greater than 0")
        }
        productRepository.findById(id).orElseThrow { IllegalArgumentException("Product not found") }
        productRepository.deleteById(id)
    }
}
