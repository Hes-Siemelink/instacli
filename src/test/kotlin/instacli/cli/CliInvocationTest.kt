package instacli.cli

import instacli.engine.CliScript
import instacli.engine.CommandInfo
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.File

val testDir: File = File("src/test/resources")

class CliInvocationTest {

    val out = MockOutput()

    @Test
    fun `Print usage`() {

        // Given
        val session = InstacliInvocation(arrayOf("-q"), workingDir = testDir, out = out)

        // When
        session.run()

        // Then
        assertTrue(out.usagePrinted)
    }

    @Test
    fun `Print directory info and commands`() {

        // Given
        val session = InstacliInvocation(arrayOf("-q", "sample"), workingDir = testDir, out = out)

        // When
        session.run()

        // Then
        assertNotNull(out.directoryInfoPrinted)
        assertEquals("sample", out.directoryInfoPrinted?.name)

        assertEquals(1, out.commandsPrinted.size)
        assertEquals("simple", out.commandsPrinted[0].name)
        assertEquals("Simple test cases", out.commandsPrinted[0].description)
    }

    @Test
    fun `Print script commands`() {

        // Given
        val session = InstacliInvocation(arrayOf("-q", "sample", "simple"), workingDir = testDir, out = out)

        // When
        session.run()

        // Then
        assertEquals(1, out.commandsPrinted.size)
        assertEquals("echo", out.commandsPrinted[0].name)
        assertEquals("Echos the input", out.commandsPrinted[0].description)
    }

    @Test
    fun `Print script info`() {

        // Given
        val session = InstacliInvocation(arrayOf("-q", "--help", "sample", "simple", "echo"), workingDir = testDir, out = out)

        // When
        session.run()

        // Then
        assertNotNull(out.scriptInfoPrinted)
        assertEquals("Echos the input", out.scriptInfoPrinted?.description)
        assertEquals("Input that becomes output", out.scriptInfoPrinted?.input?.data?.get("input")?.textValue())
    }

}

class MockOutput : UserOutput {

    var usagePrinted: Boolean = false
    var commandsPrinted = listOf<CommandInfo>()
    var directoryInfoPrinted: DirectoryInfo? = null
    var scriptInfoPrinted: CliScript? = null

    override fun printUsage() {
        usagePrinted = true
    }

    override fun printScriptInfo(script: CliScript) {
        scriptInfoPrinted = script
    }

    override fun printCommands(commands: List<CommandInfo>) {
        commandsPrinted = commands
    }

    override fun printDirectoryInfo(info: DirectoryInfo) {
        directoryInfoPrinted = info
    }
}