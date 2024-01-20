package instacli.spec

import instacli.cli.CliFile
import instacli.cli.CliFileContext
import instacli.commands.CodeExample
import instacli.commands.Connections
import instacli.commands.TestCase
import instacli.commands.userPrompt
import instacli.script.*
import instacli.util.MockUser
import org.junit.jupiter.api.DynamicContainer.dynamicContainer
import org.junit.jupiter.api.DynamicNode
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.isDirectory
import kotlin.io.path.name
import kotlin.io.path.writeText

object TestPaths {
    val RESOURCES: Path = Path.of("src/test/resources")
    val TEST_CONNECTIONS: Path = TestPaths.RESOURCES.resolve("instacli-home/connections.yaml")
    val SAMPLE_SERVER = Path.of("samples/sample-server/sample-server.cli")
}

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

    val connections = Connections.load(TestPaths.TEST_CONNECTIONS)
    connections.file = tempFile
    connections.storeIn(context)

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
            dynamicContainer(doc.name, InstacliDoc(doc).getCodeExamples())
        }.toList()
    } else {
        return InstacliDoc(this).getCodeExamples()
    }
}

private fun InstacliDoc.getCodeExamples(): List<DynamicTest> {

    // Scan document for code examples
    scan()

    // Set up test dir with helper files from document
    val testDir = Files.createTempDirectory("instacli-")
    helperFiles.forEach {
        testDir.resolve(it.key).writeText(it.value)
    }
    val connections = if (helperFiles.containsKey(Connections.FILE_NAME)) {
        Connections.load(testDir.resolve(Connections.FILE_NAME))
    } else {
        Connections()
    }

    // Generate tests
    return codeExamples
        .map {
            Script.from(it).toTest(document, CliFileContext(testDir), connections)
        }
}

private fun Script.toTest(document: Path, context: ScriptContext, connections: Connections): DynamicTest {

    connections.storeIn(context)
    userPrompt = MockUser()

    return dynamicTest(getText(CodeExample), document.toUri()) {
        try {
            runScript(context)
        } catch (a: Break) {
            a.output
        }
    }
}



