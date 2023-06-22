package yay

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import java.io.File
import java.io.FileNotFoundException

class YayTests {

    @Test
    fun assertStatementTests() {
        test("Assert statement tests.yay")
    }

    @Test
    fun callAnotherScript() {
        test("Call another script.yay")
    }

    @Test
    fun doTestRoutine() {
        test("Do-test-routine.yay")
    }

    @Test
    fun listExecution() {
        test("Do execution tests.yay")
    }

    @Test
    fun forEach() {
        test("For each usage.yay")
        test("For each and merge.yay")
    }

    @Test
    fun mergeAndJoin() {
        test("Merge data.yay")
        // TODO: test("Join handler tests.yay")
    }

    @Test
    @Disabled
    fun python() {
        test("Python handler tests.yay")
    }

    @Test
    @Disabled
    fun rawAndLive() {
        test("Raw and live.yay")
    }

    @Test
    fun files() {
        test("Read file tests.yay")
    }

    @Test
    @Timeout(3)
    fun repeat() {
        test("Repeat tests.yay")
    }

    @Test
    fun ifStatements() {
        test("If statements.yay")
    }

    @Test
    fun replace() {
        test("Replace handler tests.yay")
    }

    @Test
    fun output() {
        test("Result variable tests.yay")
        test("Set multiple variables from output.yay")
    }

    @Test
    fun variableReplacement() {
        test("Variable replacement in text.yay")
    }

    @Test
    fun sleep() {
        test("Wait tests.yay")
    }

    @Test
    fun objectCreation() {
        test("Object tests.yaml")
    }
}

fun test(resource: String) {
    testResource("/yay/${resource}")
}

fun testResource(resource: String) {
    println("Running tests for ${resource}")
    YayScript.run(toFile(resource))
}

fun toFile(resource: String): File {
    val uri = YayScript::class.java.getResource(resource)?.toURI() ?: throw FileNotFoundException(resource)
    return File(uri.path)
}
