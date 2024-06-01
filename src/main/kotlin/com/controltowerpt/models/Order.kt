package com.controltowerpt.models

import jakarta.persistence.*
import lombok.AllArgsConstructor
import lombok.Getter
import lombok.NoArgsConstructor
import lombok.Setter

@Entity
@Table(name = "\"order\"")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class Order(
    val direction: String = "",
    val state: OrderState = OrderState.PREPARING,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private var id:
        Long = 0
}

enum class OrderState {
    PREPARING,
    PREPARED,
    IN_DELIVERY,
    DELIVERED,
}
