package com.controltowerpt.loader

import com.controltowerpt.models.Warehouse
import com.controltowerpt.repositories.WarehouseRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class WarehouseLoader(private val warehouseRepository: WarehouseRepository) : CommandLineRunner {
    override fun run(vararg args: String) {
        createWarehouse()
    }

    private fun createWarehouse() {
        if (warehouseRepository.findById(1).isEmpty) {
            val warehouse = Warehouse()
            warehouse.direction = "Avenida simpatica de tuquito 123"
            warehouseRepository.save(warehouse)
        }
    }
}
