package commands.shell

import com.lordcodes.turtle.shellRun
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File

class ShellTest {

    @Test
    fun runShellScript() {
        val output = shellRun("sh", listOf("hello.sh"), File("src/test/resources"))
        assertEquals("Hello World", output)
    }
}