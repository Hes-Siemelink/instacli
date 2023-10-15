package instacli

import instacli.script.commands.TestCase
import instacli.script.execution.CliScript
import instacli.script.execution.Command
import instacli.script.files.CliScriptFile
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import java.io.File
import java.io.FileNotFoundException

fun test(scriptFileName: String) {
    println("Running tests for ${scriptFileName}")

    CliScriptFile(toFile(scriptFileName)).run()

    println()
}

fun toFile(resource: String): File {
    val testDir = File("instacli-spec/test")
    val testFile = File(testDir, resource)
    if (!testFile.exists()) {
        throw FileNotFoundException(testFile.absolutePath)
    }

    return testFile
}

fun toDynamicTests(vararg cliScripts: String): List<DynamicTest> {
    return cliScripts.map() { scriptFileName ->
        dynamicTest(scriptFileName) { test(scriptFileName) }
    }
}

private val testCaseCommandName = TestCase().name

// TODO Wire this up with dynamic containers so we expose test cases defined in the CLI scripts as tests on the jUnit level.
fun CliScript.getTestCases(): List<CliScript> {
    val allTests = mutableListOf<CliScript>()

    var currentCase = mutableListOf<Command>()
    var first = true
    for (command in commands) {
        if (command.name == testCaseCommandName) {
            if (first) {
                first = false
            } else {
                allTests.add(CliScript(currentCase))
            }
            currentCase = mutableListOf()
        }
        currentCase.add(command)
    }
    allTests.add(CliScript(currentCase)) // Don't forget the last command

    return allTests
}

