package com.controltowerpt.models

import jakarta.persistence.*
import lombok.Getter

@Entity
@Table
@Getter
class Product(
    @Column(nullable = false)
    val name: String = "",

    @Column(nullable = false)
    var price: Double = 0.0,

    @ManyToOne
    @JoinColumn(name = "shop_id")
    val shopId: Shop = Shop()
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    val id: Long = 0

}
