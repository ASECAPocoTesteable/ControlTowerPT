package com.controltowerpt.models.manytomany

import com.controltowerpt.models.Order
import com.controltowerpt.models.Product
import jakarta.persistence.*

@Entity
@Table
class ProductOrder {
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

    @ManyToOne(
        fetch = FetchType.LAZY,
    )
    @JoinColumn(
        name = "product_id",
    )
    private var product:
        Product = Product()

    @ManyToOne(
        fetch = FetchType.LAZY,
    )
    @JoinColumn(
        name = "order_id",
    )
    private var order:
        Order = Order()

    @Column(
        nullable = false,
    )
    private var amount:
        Int = 0
}
