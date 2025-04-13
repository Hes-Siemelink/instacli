package instacli.spec

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import instacli.cli.InstacliMain
import instacli.cli.reportError
import instacli.commands.connections.Credentials
import instacli.commands.connections.CredentialsFile
import instacli.commands.connections.setCredentials
import instacli.commands.testing.CodeExample
import instacli.commands.testing.ExpectedConsoleOutput
import instacli.commands.testing.StockAnswers
import instacli.commands.testing.TestCase
import instacli.commands.userinteraction.TestPrompt
import instacli.commands.userinteraction.UserPrompt
import instacli.files.*
import instacli.language.*
import instacli.util.IO
import instacli.util.Json
import instacli.util.Yaml
import instacli.util.toDisplayYaml
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.DynamicContainer.dynamicContainer
import org.junit.jupiter.api.DynamicNode
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.function.Executable
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.isDirectory
import kotlin.io.path.name
import kotlin.io.path.writeText

//
// Instacli tests
//

fun Path.getTestCases(): List<DynamicNode> {
    if (isDirectory()) {
        val pages = Files.walk(this).filter { it.name.endsWith(CLI_SCRIPT_EXTENSION) }
        return pages.map { file ->
            dynamicContainer(file.name, CliFile(file).getTestCases())
        }.toList()
    } else {
        return CliFile(this).getTestCases()
    }
}

/**
 * Gets all individual test cases in a script file as a dynamic tests.
 */
fun CliFile.getTestCases(): List<DynamicTest> {
    val context = CliFileContext(file)

    context.setCredentials(tempCredentials())

    return script.splitTestCases().map { script ->
        dynamicTest(script.getTitle(TestCase), file.toUri(), TestCaseRunner(context, script))
    }
}

private fun tempCredentials(): CredentialsFile {
    val credentials = Credentials.fromFile(TestPaths.TEST_CREDENTIALS)

    val tempFile = Files.createTempFile("instacli-connections-", ".yaml")
    tempFile.toFile().deleteOnExit()
    credentials.file = tempFile

    return credentials
}

private fun tempCredentials(testDir: Path, localCredentials: Boolean): CredentialsFile {
    return if (localCredentials) {
        Credentials.fromFile(testDir.resolve(Credentials.FILE_NAME))
    } else {
        CredentialsFile()
    }
}


class TestCaseRunner(
    val context: ScriptContext,
    val script: Script
) : Executable {

    override fun execute() {
        context.error = null
        try {
            script.run(context)
        } catch (a: Break) {
            a.output
        } catch (e: InstacliCommandError) {
            e.error.data?.let {
                println(it.toDisplayYaml())
            }
            throw e
        } catch (e: InstacliLanguageException) {
            e.reportError(printStackTrace = false)
            throw e
        }
    }
}

fun Script.getTitle(commandHandler: CommandHandler): String {
    val command = commands.find {
        it.name == TestCase.name
    }
    return command?.data?.textValue() ?: commandHandler.name
}

//
// Code examples in Markdown files
//

fun Path.getCodeExamples(): List<DynamicNode> {
    if (isDirectory()) {
        val documents = Files.walk(this).filter { it.name.endsWith(MARKDOWN_SPEC_EXTENSION) }
        return documents.map { doc ->
            dynamicContainer(doc.name, InstacliMarkdown.scan(doc).getCodeExamples())
        }.toList()
    } else {
        return InstacliMarkdown.scan(this).getCodeExamples()
    }
}

private fun InstacliMarkdown.getCodeExamples(): List<DynamicTest> {

    // Set up test dir with helper files from document
    val testDir = Files.createTempDirectory("instacli-")
    helperFiles.forEach {
        println("Helper file: ${it.key}")
        val targetFile = testDir.resolve(it.key)
        Files.createDirectories(targetFile.parent)
        targetFile.writeText(it.value)
    }
    val credentials = tempCredentials(testDir, helperFiles.containsKey(Credentials.FILE_NAME))

    // Generate tests
    val instacliTests = scriptExamples
        .map {
            toTest(document, it, CliFileContext(testDir), credentials)
        }
    val cliInvocationTests = instacliCommandExamples.map {
        val dir = it.directory ?: testDir
        it.toTest(document, dir)
    }

    return instacliTests + cliInvocationTests
}

private fun toTest(
    document: Path,
    example: UsageExample,
    context: ScriptContext,
    credentials: CredentialsFile
): DynamicTest {

    context.setCredentials(credentials)
    UserPrompt.default = TestPrompt

    val check: JsonNode? = example.getOutputCheckerScript()
    val scriptNodes = if (check != null) {
        Yaml.parseAsFile(example.content) + listOf(check)
    } else {
        Yaml.parseAsFile(example.content)
    }

    val script = Script.from(scriptNodes)
    val title = script.getTitle(CodeExample)

    return dynamicTest(title, document.toUri(), TestCaseRunner(context, script))
}

private fun UsageExample.getOutputCheckerScript(): JsonNode? {
    val expectedOutput = output ?: return null

    return Json.newObject(ExpectedConsoleOutput.name, expectedOutput)
}

//
// Command line examples
//

private fun UsageExample.toTest(document: Path, testDir: Path): DynamicTest {
    return dynamicTest("$ $command", document.toUri()) {
        testCommand(testDir)
    }
}

fun UsageExample.testCommand(testDir: Path) {
    System.err.println("Testing command: $command")
    val line = command.split("\\s+".toRegex())
    line.first() shouldBe "cli"
    val args = line.drop(1).toTypedArray()

    prepareInput(input, testDir)

    println("$ $command")

    val (stdout, stderr) = IO.captureSystemOutAndErr {
        InstacliMain.main(args, workingDir = testDir)
    }

    println(stdout)
    System.err.println(stderr)

    output?.let {
        val out = stdout.trim()
        val err = stderr.trim()
        val combined = buildString {
            if (out.isEmpty()) {
                append(err)
            } else {
                append(out)
                if (err.isNotEmpty()) {
                    append("\n")
                    append(err)
                }
            }
        }
        combined shouldBe it.trim()
    }
}

fun prepareInput(input: String?, testDir: Path) {
    input ?: return

    val inputYaml = Yaml.parse(input)
    if (inputYaml !is ObjectNode) {
        return
    }

    StockAnswers.execute(inputYaml, CliFileContext(testDir))
}
