package com.controltowerpt.repositories

import com.controltowerpt.models.Product
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class ProductRepositoryTest {
    @Autowired
    lateinit var entityManager: TestEntityManager

    @Autowired
    lateinit var productRepository: ProductRepository

    @AfterEach
    fun cleanUp() {
        productRepository.deleteAll()
    }

    @Test
    fun test001CreateProduct() {
        val product = Product(name = "Test Product", price = 10.0)

        val savedProduct = entityManager.persist(product)

        entityManager.flush()

        val retrievedProduct = savedProduct.id.let { productRepository.findById(it).orElse(null) }

        assertEquals(product.name, retrievedProduct?.name)
    }

    @Test
    fun test002UpdateProductByPrice() {
        val product = Product(name = "Test Product", price = 10.0)
        val savedProduct = entityManager.persist(product)

        entityManager.flush()

        val retrievedProduct = savedProduct.id.let { productRepository.findById(it).orElse(null) }
        assertEquals(product.name, retrievedProduct?.name)

        val rowsUpdated = productRepository.updateProductByPrice(20.0, savedProduct.id)
        entityManager.flush()

        assertEquals(1, rowsUpdated)

        entityManager.clear()

        val retrievedProduct2 = savedProduct.id.let { productRepository.findById(it).orElse(null) }
        assertEquals(20.0, retrievedProduct2?.price)
    }
}
