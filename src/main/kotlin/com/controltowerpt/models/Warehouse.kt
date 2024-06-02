package com.controltowerpt.models

import jakarta.persistence.*

@Entity
class Warehouse() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    var id: Long = 0

    var direction: String = ""
}
