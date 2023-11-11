package instacli.commands

import com.lordcodes.turtle.shellRun
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.io.File

class ShellExecutionTest {

    @Test
    fun `Run shell command`() {
        val output = shellRun("echo", listOf("Hello World"))

        output shouldBe "Hello World"
    }

    @Test
    fun `Run shell script`() {
        val output = shellRun("sh", listOf("hello.sh"), File("src/test/resources"))

        output shouldBe "Hello World"
    }

    @Test
    fun `Run unknown command`() {
        shouldThrow<Exception> {
            shellRun("unknown_command", listOf(), File("src/test/resources"))
        }
    }
}