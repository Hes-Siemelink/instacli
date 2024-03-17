package instacli.spec

import com.fasterxml.jackson.databind.node.ObjectNode
import instacli.cli.CliFile
import instacli.cli.CliFileContext
import instacli.cli.InstacliMain
import instacli.commands.CodeExample
import instacli.commands.Credentials
import instacli.commands.StockAnswers
import instacli.commands.TestCase
import instacli.script.*
import instacli.util.IO
import instacli.util.TestPrompt
import instacli.util.UserPrompt
import instacli.util.Yaml
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
    val context = CliFileContext(cliFile)

    val tempFile = Files.createTempFile("instacli-connections-", ".yaml")
    tempFile.toFile().deleteOnExit()

    val credentials = Credentials.load(TestPaths.TEST_CREDENTIALS)
    credentials.file = tempFile
    credentials.storeIn(context)

    return script.getTestCases().map { script ->
        dynamicTest(script.getText(TestCase), cliFile.toUri()) {
            try {
                script.runScript(context)
            } catch (a: Break) {
                a.output
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
        val documents = Files.walk(this).filter { it.name.endsWith(".md") }
        return documents.map { doc ->
            dynamicContainer(doc.name, InstacliDoc.scan(doc).getCodeExamples())
        }.toList()
    } else {
        return InstacliDoc.scan(this).getCodeExamples()
    }
}

private fun InstacliDoc.getCodeExamples(): List<DynamicTest> {

    // Set up test dir with helper files from document
    val testDir = Files.createTempDirectory("instacli-")
    helperFiles.forEach {
        testDir.resolve(it.key).writeText(it.value)
    }
    val credentials = if (helperFiles.containsKey(Credentials.FILE_NAME)) {
        Credentials.load(testDir.resolve(Credentials.FILE_NAME))
    } else {
        Credentials()
    }

    // Generate tests
    val codeExampleTests = codeExamples
        .map {
            Script.from(it).toTest(document, CliFileContext(testDir), credentials)
        }
    val cliInvocationTests = commandExamples.map {
        val dir = it.directory ?: testDir
        it.toTest(document, dir)
    }

    return codeExampleTests + cliInvocationTests
}

private fun Script.toTest(document: Path, context: ScriptContext, credentials: Credentials): DynamicTest {

    credentials.storeIn(context)
    UserPrompt.default = TestPrompt

    return dynamicTest(getText(CodeExample), document.toUri()) {
        try {
            runScript(context)
        } catch (a: Break) {
            a.output
        }
    }
}

//
// Command line examples
//

private fun CommandExample.toTest(document: Path, testDir: Path): DynamicTest {
    return dynamicTest("$ $command", document.toUri()) {
        testCommand(testDir)
    }
}

fun CommandExample.testCommand(testDir: Path) {
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
