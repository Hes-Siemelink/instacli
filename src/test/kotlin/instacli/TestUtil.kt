package instacli

import instacli.script.commands.TestCase
import instacli.script.execution.CliScript
import instacli.script.execution.Command
import instacli.script.files.CliScriptFile
import instacli.script.files.ScriptDirectoryContext
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

/**
 * Gets all individual test cases in a script file as a dynamic tests.
 */
fun CliScriptFile.getTestCases(): List<DynamicTest> {
    val context = ScriptDirectoryContext(scriptFile)
    return cliScript.getTestCases().map() {
        dynamicTest(it.getTestName()) { it.run(context) }
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
