package hes.yak

import org.junit.jupiter.api.Test
import java.io.File
import java.io.FileNotFoundException

class YakTest {

    @Test
    fun assertStatementTests() {
        test("/yay/Assert statement tests.yay")
    }

    @Test
    fun callAnotherScript() {
        test("/yay/Call another script.yay")
    }

    @Test
    fun doTestRoutine() {
        test("/yay/Do-test-routine.yay")
    }
}

fun test(resource:String) {
    YakScript.run(toFile(resource))
}

fun toFile(resource: String): File {
    val uri = YakScript::class.java.getResource(resource)?.toURI() ?: throw FileNotFoundException(resource)
    return File(uri.path)
}
