package com.controltowerpt.models

import jakarta.persistence.*

@Entity
@Table
class Product {
    @Id
    @GeneratedValue(
        strategy = GenerationType.IDENTITY,
    )
    @Column(
        nullable = false,
        unique = true,
    )
    private var id:
        Long = 0

    @Column(
        nullable = false,
    )
    private var name:
        String = ""

    @Column(
        nullable = false,
    )
    private var price:
        Double = 0.0

    @ManyToOne
    @JoinColumn(
        name = "shop_id",
    )
    private var shopId:
        Shop = Shop()
}
