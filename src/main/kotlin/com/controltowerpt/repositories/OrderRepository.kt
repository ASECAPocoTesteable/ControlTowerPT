package com.controltowerpt.repositories

import com.controltowerpt.models.Order
import org.springframework.data.jpa.repository.JpaRepository

interface OrderRepository : JpaRepository<Order, Long>
