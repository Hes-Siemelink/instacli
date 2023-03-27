package hes.yak

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
    fun merge() {
        test("Merge data.yay")
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
}

fun test(resource:String) {
    println("Running tests for ${resource}")
    YakScript.run(toFile("/yay/${resource}"))
}

fun toFile(resource: String): File {
    val uri = YakScript::class.java.getResource(resource)?.toURI() ?: throw FileNotFoundException(resource)
    return File(uri.path)
}
