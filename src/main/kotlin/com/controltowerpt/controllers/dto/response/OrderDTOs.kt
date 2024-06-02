package com.controltowerpt.controllers.dto.response

import com.controltowerpt.models.Order
import com.controltowerpt.models.Product
import java.io.Serializable

class OrderInfoDTO : Serializable {
    var id: Long = 0
    var clientDirection: String = ""
    var state: String = ""
    var warehouseDirection: String = ""
    var productOrders: List<ProductOrderDTO> = listOf()

    fun fromOrder(order: Order): OrderInfoDTO {
        id = order.id
        clientDirection = order.clientDirection
        state = order.state.toString()
        warehouseDirection = order.warehouse?.direction ?: ""
        productOrders =
            order.productOrders.map {
                val productOrderDTO = ProductOrderDTO()
                productOrderDTO.quantity = it.amount
                productOrderDTO.fromProduct(it.product)
                productOrderDTO
            }
        return this
    }
}

class ProductOrderDTO : Serializable {
    var id: Long = 0
    var name: String = ""
    var price: Double = 0.0
    var quantity: Int = 0

    fun fromProduct(product: Product): ProductOrderDTO {
        id = product.id
        name = product.name
        price = product.price
        return this
    }
}
