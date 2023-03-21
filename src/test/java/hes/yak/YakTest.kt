package hes.yak

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class YakTest {

    @Test
    fun loadScript() {

        val script = YakScript.load("/yay/Assert statement tests.yay")

        Assertions.assertThat(script.contents.contains("Test case: Assert equals with nested objects"))
    }
}