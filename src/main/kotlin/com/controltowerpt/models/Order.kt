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
class Order {
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
        name = "client_id",
    )
    private var clientId: Client = Client()

    @ManyToOne(
        fetch = FetchType.LAZY,
    )
    @JoinColumn(
        name = "shop_id",
    )
    private var shopId: Shop = Shop()
}
