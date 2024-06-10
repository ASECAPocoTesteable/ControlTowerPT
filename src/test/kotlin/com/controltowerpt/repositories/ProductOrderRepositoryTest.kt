package com.controltowerpt.repositories

import com.controltowerpt.models.Order
import com.controltowerpt.models.Product
import com.controltowerpt.models.manytomany.ProductOrder
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class ProductOrderRepositoryTest {
    @Autowired
    lateinit var entityManager : TestEntityManager

    @Autowired
    lateinit var productOrderRepository : ProductOrderRepository

    @AfterEach
    fun cleanUp() {
        productOrderRepository.deleteAll()
    }


    @Test
    fun test001findOrderByIdSuccess() {
        val product = Product(name = "Test Product", price = 10.0)
        val savedProduct = entityManager.persist(product)
        val order = Order()

        val productOrder = ProductOrder(product = savedProduct, order = order, amount = 1)

        order.productOrders.add(productOrder)
        val savedOrder = entityManager.persist(order)

        val productOrders = productOrderRepository.findByOrderId(savedOrder.id)

        assertEquals(1, productOrders.size)

        val productOrderFound = productOrders[0]

        assertEquals(product.id, productOrderFound.product.id)
    }

    @Test
    fun test002findOrderByIdFailure() {
        val product = Product(name = "Test Product", price = 10.0)
        val savedProduct = entityManager.persist(product)
        val order = Order()

        val productOrder = ProductOrder(product = savedProduct, order = order, amount = 1)

        order.productOrders.add(productOrder)
        val savedOrder = entityManager.persist(order)

        val productOrders = productOrderRepository.findByOrderId(savedOrder.id + 1)

        assertEquals(0, productOrders.size)
    }
}