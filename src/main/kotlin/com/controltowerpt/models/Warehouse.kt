package com.controltowerpt.models

import jakarta.persistence.*
import lombok.Getter

@Entity
@Table
@Getter
class Warehouse {
    @Id
    @GeneratedValue(
        strategy = GenerationType.IDENTITY,
    )
    @Column(
        nullable = false,
        unique = true,
    )
    private var id: Long = 0

    @Column(
        nullable = false,
    )
    private var direction: String = ""

    @ManyToMany(mappedBy = "warehouses")
    private val shops: Set<Shop> = HashSet()
}
