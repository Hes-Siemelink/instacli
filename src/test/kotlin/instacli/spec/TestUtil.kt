package instacli.spec

import instacli.cli.CliFile
import instacli.cli.CliFileContext
import instacli.commands.*
import instacli.script.Break
import instacli.script.Command
import instacli.script.Script
import instacli.util.MockUser
import org.junit.jupiter.api.DynamicContainer
import org.junit.jupiter.api.DynamicContainer.dynamicContainer
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.name
import kotlin.io.path.writeText

object TestPaths {
    val resources: Path = Path.of("src/test/resources")
}

//
// Instacli tests in Instacli
//

fun getAllInstacliTests(directory: Path): List<DynamicContainer> {
    val pages = Files.walk(directory).filter { it.name.endsWith(".cli") }
    return pages.map { file ->
        dynamicContainer(file.name, CliFile(file).getTestCases())
    }.toList()
}

private val TEST_CASE = TestCase().name
private const val TEST_CONNECTIONS = "instacli-home/connections.yaml"

/**
 * Gets all individual test cases in a script file as a dynamic tests.
 */
fun CliFile.getTestCases(): List<DynamicTest> {
    val context = CliFileContext(cliFile, connections = Connections.load(TEST_CONNECTIONS))

    val tempFile = Files.createTempFile("instacli-connections-", ".yaml")
    tempFile.toFile().deleteOnExit()
    context.connections.file = tempFile

    return script.getTestCases().map {
        dynamicTest(it.getTestName(), cliFile.toUri()) {
            try {
                it.runScript(context)
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
    var first = true
    for (command in commands) {
        if (command.name == TEST_CASE) {
            if (first) {
                // Ignore everything before the first 'Test case' command
                first = false
            } else {
                // Adds everything that was recorded since the 'Test case' command
                allTests.add(Script(currentCase))
            }
            currentCase = mutableListOf()
        }
        currentCase.add(command)
    }
    // Add the last command
    allTests.add(Script(currentCase))

    return allTests
}

fun Script.getTestName(nameCommand: String = TEST_CASE): String {
    val testCaseCommand = commands.find {
        it.name == nameCommand
    }
    return testCaseCommand?.data?.textValue() ?: nameCommand
}

//
// Code examples in Markdown files
//

fun getCodeExamplesInDocument(file: Path): List<DynamicTest> {

    // Scan document for code examples
    val doc = InstacliDoc(file)
    doc.scan()

    // Set up test dir with helper files from document
    val testDir = Files.createTempDirectory("instacli-")
    doc.helperFiles.forEach {
        testDir.resolve(it.key).writeText(it.value)
    }
    val connections = if (doc.helperFiles.containsKey(CONNECTIONS_YAML)) {
        Connections.load(testDir.resolve(CONNECTIONS_YAML))
    } else {
        Connections()
    }

    // Generate tests
    return doc.codeExamples
        .map {
            val script = Script.from(it)
            val testContext = CliFileContext(testDir, connections = connections)
            userPrompt = MockUser()
            DynamicTest.dynamicTest(script.getTestName(CodeExample().name), file.toUri()) {
                try {
                    script.runScript(testContext)
                } catch (a: Break) {
                    a.output
                }
            }
        }
}

fun getCodeExamplesInAllFiles(directory: Path): List<DynamicContainer> {
    val pages = Files.walk(directory).filter { it.name.endsWith(".md") }
    return pages.map { file ->
        DynamicContainer.dynamicContainer(file.name, getCodeExamplesInDocument(file))
    }.toList()
}

