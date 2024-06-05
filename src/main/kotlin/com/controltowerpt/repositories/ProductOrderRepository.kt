package com.controltowerpt.repositories

import com.controltowerpt.models.manytomany.ProductOrder
import org.springframework.data.jpa.repository.JpaRepository

interface ProductOrderRepository : JpaRepository<ProductOrder, Long> {
    fun findByOrderId(id: Long): List<ProductOrder>
}
