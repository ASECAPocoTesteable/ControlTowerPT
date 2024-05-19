package com.controltowerpt.repositories

import com.controltowerpt.models.Shop
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ShopRepository : JpaRepository<Shop, Long>
