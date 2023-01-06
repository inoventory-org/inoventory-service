package com.inovex.inoventory

import com.inovex.inoventory.mock.TestConfig
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration

@SpringBootTest
@ContextConfiguration(classes = [TestConfig::class])
class InoventoryApplicationTests {

    @Test
    fun contextLoads() {
    }

}
