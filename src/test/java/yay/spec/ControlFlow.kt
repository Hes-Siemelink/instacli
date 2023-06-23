package yay.spec

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import yay.test

class ControlFlow {

    @Test
    fun `do`() {
        test("control-flow/Do tests.yay")
    }

    @Test
    fun `if`() {
        test("control-flow/If tests.yay")
    }

    @Test
    fun forEach() {
        test("control-flow/For each tests.yay")
    }

    @Test
    @Timeout(3)
    fun repeat() {
        test("control-flow/Repeat tests.yay")
    }

    @Test
    fun doTestRoutine() {
        test("control-flow/Do-test-routine.yay")
    }

    @Test
    fun executeYayFile() {
        test("control-flow/Execute yay file tests.yay")
    }

}


