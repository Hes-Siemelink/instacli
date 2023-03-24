package hes.yak

import org.junit.jupiter.api.Test

class YakTest {

    @Test
    fun assertStatementTests() {
        YakScript.run("/yay/Assert statement tests.yay")
    }

    @Test
    fun callAnotherScript() {
        YakScript.run("/yay/Call another script.yay")
    }

    @Test
    fun doTestRoutine() {
        YakScript.run("/yay/Do-test-routine.yay")
    }
}