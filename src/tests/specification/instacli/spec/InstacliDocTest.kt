package instacli.spec

import instacli.cli.CliCommandLineOptions
import instacli.cli.InstacliMain
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.junit.jupiter.api.Test
import java.nio.file.Path

val REFERENCE_DOC: Path = TestPaths.SPEC.resolve("reference/documentation/writing-instacli-docs.md")

class InstacliDocTest {

    private val doc = InstacliDoc.scan(REFERENCE_DOC)

    @Test
    fun `Code examples`() {
        doc.codeExamples.size shouldBe 4
        doc.codeExamples[0] shouldContain "Code example: An Instacli snippet inside Markdown"
        doc.codeExamples[2] shouldContain "Stock answers:"
    }

    @Test
    fun `Yaml files`() {
        doc.helperFiles.size shouldBe 1
        doc.helperFiles.keys shouldContain "data.yaml"
        doc.helperFiles["data.yaml"] shouldBe "key: value"
    }

    @Test
    fun `Command line invocation`() {
        doc.commandExamples.size shouldBe 1
        doc.commandExamples[0].command shouldBe "cli --help"
        doc.commandExamples[0].output shouldContain "Instantly create CLI applications with light scripting!"
    }

    @Test
    fun `Example cli command test`() {
        val output = captureSystemOut {
            testCliCommand(doc.commandExamples[0])
        }

        output.trim() shouldBe doc.commandExamples[0].output?.trim()
    }

    private fun testCliCommand(command: CommandExample) {
        val line = command.command.split("\\s+".toRegex())
        line.first() shouldBe "cli"
        val options = CliCommandLineOptions(line.drop(1))

        InstacliMain(options).run()
    }

    @Test
    fun `capture println`() {
        val string = captureSystemOut {
            println("Boo!")
        }
        string shouldBe "Boo!\n"
    }
}
