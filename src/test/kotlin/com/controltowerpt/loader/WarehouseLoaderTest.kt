package com.controltowerpt.loader

import com.controltowerpt.models.Warehouse
import com.controltowerpt.repositories.WarehouseRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever


class WarehouseLoaderTest {

    @Mock
    private lateinit var warehouseRepository: WarehouseRepository

    @InjectMocks
    private lateinit var warehouseLoader: WarehouseLoader

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun test001RunLoaderShouldNotAdd() {
        val warehouse = Warehouse()
        whenever(warehouseRepository.findAll()).thenReturn(listOf(warehouse))
        warehouseLoader.run()
        Mockito.reset(warehouseRepository)
        assertTrue(warehouseRepository.findAll().isEmpty())
    }

    @Test
    fun test002RunLoaderSuccessful(){
        whenever(warehouseRepository.findAll()).thenReturn(listOf())
        warehouseLoader.run()
        Mockito.verify(warehouseRepository).save(Mockito.any(Warehouse::class.java))
    }
}