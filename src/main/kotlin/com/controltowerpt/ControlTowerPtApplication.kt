package com.controltowerpt

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ControlTowerPtApplication

fun main(args: Array<String>) {
    val args = arrayOf<String>()
    runApplication<ControlTowerPtApplication>(
        *args,
    )
}
