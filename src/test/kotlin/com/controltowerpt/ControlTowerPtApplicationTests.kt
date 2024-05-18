package com.controltowerpt

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ControlTowerPtApplicationTests {
    @Test
    fun test001Test1plus1() {
        assertThat(
            1 + 1,
        ).isEqualTo(
            2,
        )
    }
}
