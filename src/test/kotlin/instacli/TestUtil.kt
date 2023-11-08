package instacli

import instacli.cli.CliScriptFile
import instacli.cli.ScriptFileContext
import instacli.cli.add
import instacli.commands.TestCase
import instacli.engine.Break
import instacli.engine.CliScript
import instacli.engine.Command
import instacli.util.Yaml
import org.junit.jupiter.api.DynamicContainer
import org.junit.jupiter.api.DynamicContainer.dynamicContainer
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import java.io.File
import java.io.FileNotFoundException

fun loadTestCases(vararg cliScripts: String): List<DynamicContainer> {
    return cliScripts.map() { scriptFileName ->
        dynamicContainer(scriptFileName, CliScriptFile(toFile(scriptFileName)).getTestCases())
    }
}

fun toFile(resource: String): File {
    val testDir = File("instacli-spec/test")
    val testFile = File(testDir, resource)
    if (!testFile.exists()) {
        throw FileNotFoundException(testFile.absolutePath)
    }

    return testFile
}

private val TEST_CASE = TestCase().name
private val TEST_CONNECTIONS by lazy { Yaml.readResource("instacli-home/connections.yaml") }

/**
 * Gets all individual test cases in a script file as a dynamic tests.
 */
fun CliScriptFile.getTestCases(): List<DynamicTest> {
    val context = ScriptFileContext(scriptFile)
    context.connections.add(TEST_CONNECTIONS)
    return cliScript.getTestCases().map() {
        dynamicTest(it.getTestName()) {
            try {
                it.run(context)
            } catch (a: Break) {
                a.output
            }
        }
    }
}

/**
 * Gets all test cases in a script as a separate CliScript.
 */
fun CliScript.getTestCases(): List<CliScript> {

    val allTests = mutableListOf<CliScript>()

    var currentCase = mutableListOf<Command>()
    var first = true
    for (command in commands) {
        if (command.name == TEST_CASE) {
            if (first) {
                // Ignore everything before the first 'Test case' command
                first = false
            } else {
                // Adds everything that was recorded since the 'Test case' command
                allTests.add(CliScript(currentCase))
            }
            currentCase = mutableListOf()
        }
        currentCase.add(command)
    }
    // Add the last command
    allTests.add(CliScript(currentCase))

    return allTests
}

fun CliScript.getTestName(): String {
    val testCaseCommand = commands.find {
        it.name == TEST_CASE
    }
    return testCaseCommand?.data?.textValue() ?: TEST_CASE
}
