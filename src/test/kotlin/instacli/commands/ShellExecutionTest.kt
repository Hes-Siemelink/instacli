package instacli.commands

import com.lordcodes.turtle.shellRun
import instacli.spec.TestPaths
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class ShellExecutionTest {

    @Test
    fun `Run shell command`() {
        val output = shellRun("echo", listOf("Hello World"))

        output shouldBe "Hello World"
    }

    @Test
    fun `Run shell script`() {
        val output = shellRun("sh", listOf("hello.sh"), TestPaths.RESOURCES.toFile())

        output shouldBe "Hello World"
    }

    @Test
    fun `Run unknown command`() {
        shouldThrow<Exception> {
            shellRun("unknown_command", listOf(), TestPaths.RESOURCES.toFile())
        }
    }
}