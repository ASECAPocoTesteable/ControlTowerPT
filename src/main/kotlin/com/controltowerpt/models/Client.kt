package com.controltowerpt.models

import jakarta.persistence.*
import lombok.Getter

@Entity
@Table
@Getter
class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private var id: Long = 0

    @Column(nullable = false)
    private val email = ""

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private var shopId: Shop = Shop()
}
