package instacli.cli

import instacli.TestPaths
import instacli.files.DirectoryInfo
import instacli.language.CommandInfo
import instacli.language.Script
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


class CommandInvocationTest {

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
        out.scriptInfoPrinted?.info?.description shouldBe "Echos the input"
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
        out.outputPrinted shouldBe true
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
        out.outputPrinted shouldBe false
    }
}

class MockOutput : ConsoleOutput {

    var usagePrinted: Boolean = false
    var commandsPrinted = listOf<CommandInfo>()
    var directoryInfoPrinted: DirectoryInfo? = null
    var scriptInfoPrinted: Script? = null
    var outputPrinted: Boolean = false

    override fun printUsage(globalOptions: CommandLineParameters) {
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

    override fun printOutput(output: String) {
        outputPrinted = true
    }
}