package com.controltowerpt.repositories

import com.controltowerpt.models.Order
import com.controltowerpt.models.OrderState
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class OrderRepositoryTest {
    @Autowired
    lateinit var entityManager: TestEntityManager

    @Autowired
    lateinit var orderRepository: OrderRepository

    @Test
    fun test001CreateOrderSuccessfully() {
        val order = Order("La casa de juan", OrderState.DELIVERED)
    }
}
