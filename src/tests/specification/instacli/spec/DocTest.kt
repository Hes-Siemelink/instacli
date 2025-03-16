package instacli.spec

import instacli.cli.CliCommandLineOptions
import instacli.cli.InstacliMain
import instacli.doc.InstacliMarkdown
import instacli.doc.UsageExample
import instacli.util.IO
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.junit.jupiter.api.Test
import java.nio.file.Path

val REFERENCE_DOC: Path = TestPaths.SPEC.resolve("language/Writing Instacli documentation.spec.md")

class DocTest {

    private val doc = InstacliMarkdown.scan(REFERENCE_DOC)

    @Test
    fun `Code examples`() {
        doc.scriptExamples.size shouldBe 4
        doc.scriptExamples[0].command shouldContain "Code example: An Instacli snippet inside Markdown"
        doc.scriptExamples[2].content shouldContain "Stock answers:"
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
        val output = IO.captureSystemOut {
            testCliCommand(doc.commandExamples[0])
        }

        output.trim() shouldBe doc.commandExamples[0].output?.trim()
    }

    private fun testCliCommand(command: UsageExample) {
        val line = command.command.split("\\s+".toRegex())
        line.first() shouldBe "cli"
        val options = CliCommandLineOptions(line.drop(1))

        InstacliMain(options).run()
    }

    @Test
    fun `capture println`() {
        val string = IO.captureSystemOut {
            println("Boo!")
        }
        string shouldBe "Boo!\n"
    }
}
