package instacli.script.commands.shell

import com.lordcodes.turtle.shellRun
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.File

class ShellExecutionTest {

    @Test
    fun `Run shell command`() {
        val output = shellRun("echo", listOf("Hello World"))
        assertEquals("Hello World", output)
    }

    @Test
    fun `Run shell script`() {
        val output = shellRun("sh", listOf("hello.sh"), File("src/test/resources"))
        assertEquals("Hello World", output)
    }

    @Test
    fun `Run binary`() {
        val output = shellRun("java", listOf("-version"), File("src/test/resources"))
        assertNotNull(output)
    }

    @Test
    fun `Run unknown command`() {
        assertThrows<Exception> { shellRun("unknown_command", listOf(), File("src/test/resources")) }
    }

}