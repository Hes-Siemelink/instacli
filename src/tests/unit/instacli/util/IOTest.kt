package instacli.util

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class IOTest {

    @Test
    fun `capture println`() {
        val string = IO.captureSystemOut {
            println("Boo!")
        }
        string shouldBe "Boo!\n"
    }
}