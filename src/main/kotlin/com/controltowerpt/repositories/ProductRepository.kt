package com.controltowerpt.repositories

import com.controltowerpt.models.Product
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository : JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p WHERE p.shop.id = :shopId")
    fun findByShopId(shopId: Long): List<Product>

    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.price = :price WHERE p.id = :id")
    fun updateProductByPrice(
        @Param("price") price: Double,
        @Param("id") id: Long,
    ): Int
}
