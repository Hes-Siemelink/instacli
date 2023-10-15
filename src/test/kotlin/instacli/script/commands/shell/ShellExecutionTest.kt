package instacli.script.commands.shell

import com.lordcodes.turtle.shellRun
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
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
    fun `Run binary`() {
        val output = shellRun("java", listOf("-version"), File("src/test/resources"))

        output shouldNotBe null
    }

    @Test
    fun `Run unknown command`() {
        shouldThrow<Exception> {
            shellRun("unknown_command", listOf(), File("src/test/resources"))
        }
    }
}