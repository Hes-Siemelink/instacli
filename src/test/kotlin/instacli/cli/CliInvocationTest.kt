package instacli.cli

import instacli.script.CommandInfo
import instacli.script.Script
import instacli.spec.TestPaths
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


class CliInvocationTest {

    private var out = MockOutput()

    @BeforeEach
    fun resetOutput() {
        out = MockOutput()
    }

    @Test
    fun `Print usage`() {

        // Given
        val session = InstacliMain("-q", workingDir = TestPaths.RESOURCES, output = out)

        // When
        session.run()

        // Then
        out.usagePrinted shouldBe true
    }

    @Test
    fun `Print directory info and commands`() {

        // Given
        val session = InstacliMain("-q", "sample", workingDir = TestPaths.RESOURCES, output = out)

        // When
        session.run()

        // Then
        out.directoryInfoPrinted?.name shouldBe "sample"

        out.commandsPrinted.size shouldBe 1
        out.commandsPrinted[0].name shouldBe "simple"
        out.commandsPrinted[0].description shouldBe "Simple test cases"
    }

    @Test
    fun `Print script commands`() {

        // Given
        val session =
            InstacliMain("-q", "sample", "simple", workingDir = TestPaths.RESOURCES, output = out)

        // When
        session.run()

        // Then
        out.commandsPrinted.size shouldBe 1
        out.commandsPrinted[0].name shouldBe "echo"
        out.commandsPrinted[0].description shouldBe "Echos the input"
    }

    @Test
    fun `Print script info`() {

        // Given
        val session =
            InstacliMain(
                "-q", "--help", "sample", "simple", "echo",
                workingDir = TestPaths.RESOURCES,
                output = out
            )

        // When
        session.run()

        // Then
        out.scriptInfoPrinted?.description shouldBe "Echos the input"
        out.scriptInfoPrinted?.input?.data?.get("text")?.textValue() shouldBe "Input that becomes output"
    }

    @Test
    fun `Print output - yes`() {

        // Given
        val session = InstacliMain(
            "-q", "-o", "sample", "simple", "echo", "--text", "Script output",
            workingDir = TestPaths.RESOURCES,
            output = out
        )

        // When
        session.run()

        // Then
        out.messagePrinted shouldBe "Script output"
    }

    @Test
    fun `Print output - no`() {

        // Given
        val session = InstacliMain(
            "-q", "sample", "simple", "echo", "--text", "Script output",
            workingDir = TestPaths.RESOURCES,
            output = out
        )

        // When
        session.run()

        // Then
        out.messagePrinted shouldBe null
    }
}

class MockOutput : UserOutput {

    var usagePrinted: Boolean = false
    var commandsPrinted = listOf<CommandInfo>()
    var directoryInfoPrinted: DirectoryInfo? = null
    var scriptInfoPrinted: Script? = null
    var messagePrinted: String? = null

    override fun printUsage() {
        usagePrinted = true
    }

    override fun printScriptInfo(script: Script) {
        scriptInfoPrinted = script
    }

    override fun printCommands(commands: List<CommandInfo>) {
        commandsPrinted = commands
    }

    override fun printDirectoryInfo(info: DirectoryInfo) {
        directoryInfoPrinted = info
    }

    override fun println(message: String) {
        messagePrinted = message
    }
}