package com.controltowerpt.repositories

import com.controltowerpt.models.Shop
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class ShopRepositoryTest {
    @Autowired
    lateinit var entityManager: TestEntityManager

    @Autowired
    lateinit var shopRepository: ShopRepository

    @Test
    fun test001CreateShop() {
        val shop = Shop(name = "Test Shop")

        val savedShop = entityManager.persist(shop)

        entityManager.flush()

        val retrievedShop = savedShop.id?.let { shopRepository.findById(it).orElse(null) }
        assertEquals(shop.name, retrievedShop?.name)
        if (retrievedShop != null) {
            assertEquals(1, retrievedShop.id)
        }
    }

    @Test
    fun test002FindShopByIdShouldBeSuccesful()  {
        val shop = Shop(name = "Test Shop")
        val savedShop = entityManager.persist(shop)
        entityManager.flush()
        val retrievedShop = savedShop.id?.let { shopRepository.findById(it).orElse(null) }
        assertEquals(shop.name, retrievedShop?.name)
        if (retrievedShop != null) {
            assertEquals(1, retrievedShop.id)
        }
    }

    @Test
    fun test003FindShopByIdIsNotExistantShouldReturnNull()  {
        val shop = Shop(name = "Test Shop")
        entityManager.persist(shop)
        entityManager.flush()
        val retrievedShop = shopRepository.findById(2).orElse(null)
        assertEquals(null, retrievedShop)
    }

    @Test
    fun test004DeleteShopByIdShouldBeSuccesful()  {
        val shop = Shop(name = "Test Shop")
        val savedShop = entityManager.persist(shop)
        entityManager.flush()
        shopRepository.deleteById(savedShop.id!!)
        val retrievedShop = shopRepository.findById(savedShop.id!!).orElse(null)
        assertEquals(null, retrievedShop)
    }

    @Test
    fun test005DeleteShopByIdIsNotExistantShouldReturnNull()  {
        val shop = Shop(name = "Test Shop")
        entityManager.persist(shop)
        entityManager.flush()
        shopRepository.deleteById(2)
        val retrievedShop = shopRepository.findById(2).orElse(null)
        assertEquals(null, retrievedShop)
    }
}
