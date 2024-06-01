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
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    var id: Long = 0
}
