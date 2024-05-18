package com.controltowerpt.models

import jakarta.persistence.*
import lombok.Getter
import lombok.Setter

@Entity
@Table(name = "shop")
@Getter
@Setter
class Shop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private val id: Long? = null

    @Column(nullable = false)
    private val name: String = ""

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "shop_warehouse",
        joinColumns = [JoinColumn(name = "shop_id")],
        inverseJoinColumns = [JoinColumn(name = "warehouse_id")],
    )
    private val warehouses: Set<Warehouse> = HashSet()
}
