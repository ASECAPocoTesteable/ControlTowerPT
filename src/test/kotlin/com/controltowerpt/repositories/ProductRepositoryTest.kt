package com.controltowerpt.repositories

import com.controltowerpt.models.Product
import com.controltowerpt.models.Shop
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

    @Autowired
    lateinit var shopRepository: ShopRepository

    @AfterEach
    fun cleanUp() {
        productRepository.deleteAll()
        shopRepository.deleteAll()
    }

    @Test
    fun test001CreateProduct() {
        val shop = Shop(name = "Test Shop")

        val savedShop = entityManager.persist(shop)

        val product = Product(name = "Test Product", price = 10.0, shopId = savedShop)

        val savedProduct = entityManager.persist(product)

        entityManager.flush()

        val retrievedProduct = savedProduct.id.let { productRepository.findById(it).orElse(null) }

        assertEquals(product.name, retrievedProduct?.name)
    }

    @Test
    fun test002FindProductByStoreId() {
        val shop = Shop(name = "Test Shop")

        val savedShop = entityManager.persist(shop)

        val product = Product(name = "Test Product", price = 10.0, shopId = savedShop)

        val product2 = Product(name = "Test Product 2", price = 20.0, shopId = savedShop)

        val savedProduct = entityManager.persist(product)

        val saved2Product = entityManager.persist(product2)

        entityManager.flush()

        val retrievedProduct = savedShop.id?.let { productRepository.findByShopId(it) }

        assertEquals(2, retrievedProduct?.size)

        assertEquals(product.name, retrievedProduct?.get(0)?.name)

        assertEquals(product2.name, retrievedProduct?.get(1)?.name)
    }

    @Test
    fun test003FindProductByStoreIDShouldBeNullIfNotProductsSaveWithThatStore() {
        val shop = Shop(name = "Test Shop")

        val savedShop = entityManager.persist(shop)

        val product = Product(name = "Test Product", price = 10.0, shopId = savedShop)

        val savedProduct = entityManager.persist(product)

        entityManager.flush()

        val retrievedProduct = shopRepository.findById(savedShop.id!!).let { productRepository.findByShopId(it.get().id!!) }

        assertEquals(1, retrievedProduct.size)

        assertEquals(product.name, retrievedProduct.get(0).name)

        val shop2 = Shop(name = "Test Shop 2")

        val savedShop2 = entityManager.persist(shop2)

        val retrievedProduct2 = shopRepository.findById(savedShop2.id!!).let { productRepository.findByShopId(it.get().id!!) }

        assertEquals(0, retrievedProduct2.size)
    }

    @Test
    fun test004UpdateProductByPrice() {
        val shop = Shop(name = "Test Shop")
        val savedShop = entityManager.persist(shop)

        val product = Product(name = "Test Product", price = 10.0, shopId = savedShop)
        val savedProduct = entityManager.persist(product)

        entityManager.flush()

        val retrievedProduct = savedProduct.id.let { productRepository.findById(it).orElse(null) }
        assertEquals(product.name, retrievedProduct?.name)

        val rowsUpdated = productRepository.updateProductByPrice(20.0, savedProduct.id!!)
        entityManager.flush()

        assertEquals(1, rowsUpdated)

        entityManager.clear()

        val retrievedProduct2 = savedProduct.id.let { productRepository.findById(it).orElse(null) }
        assertEquals(20.0, retrievedProduct2?.price)
    }

}