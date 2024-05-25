package com.controltowerpt.models

import jakarta.persistence.*
import lombok.Getter

@Entity
@Table
@Getter
class Shop(
    @Column(nullable = false)
    val name: String = "",
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    val id: Long? = null,
) {
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "shop_warehouse",
        joinColumns = [JoinColumn(name = "shop_id")],
        inverseJoinColumns = [JoinColumn(name = "warehouse_id")],
    )
    val warehouses: Set<Warehouse> = HashSet()
}
