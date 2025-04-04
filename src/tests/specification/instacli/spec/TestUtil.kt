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
        val pages = Files.walk(this).filter { it.name.endsWith(".cli") }
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

    val credentials = Credentials.fromFile(TestPaths.TEST_CREDENTIALS)

    val tempFile = Files.createTempFile("instacli-connections-", ".yaml")
    tempFile.toFile().deleteOnExit()
    credentials.file = tempFile

    context.setCredentials(credentials)

    return script.getTestCases().map { script ->
        dynamicTest(script.getText(TestCase), file.toUri()) {
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
}

/**
 * Gets all test cases in a script as a separate CliScript.
 */
fun Script.getTestCases(): List<Script> {

    val allTests = mutableListOf<Script>()

    var currentCase = mutableListOf<Command>()
    var testCaseFound = false
    for (command in commands) {
        if (command.name == TestCase.name) {
            if (!testCaseFound) {
                // Ignore everything before the first 'Test case' command
                testCaseFound = true
            } else {
                // Add everything that was recorded since the 'Test case' command
                allTests.add(Script(currentCase))
            }
            currentCase = mutableListOf()
        }

        currentCase.add(command)
    }

    // Add the last test case
    if (testCaseFound) {
        allTests.add(Script(currentCase))
    }

    return allTests
}

fun Script.getText(commandHandler: CommandHandler): String {
    val command = commands.find {
        it.name == commandHandler.name
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
    val credentials = if (helperFiles.containsKey(Credentials.FILE_NAME)) {
        Credentials.fromFile(testDir.resolve(Credentials.FILE_NAME))
    } else {
        CredentialsFile()
    }

    // Generate tests
    val instacliTests = scriptExamples
        .map {
            toTest(document, it, CliFileContext(testDir), credentials)
        }
    val cliInvocationTests = commandExamples.map {
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
    val title = script.getText(CodeExample)

    return dynamicTest(title, document.toUri()) {
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
