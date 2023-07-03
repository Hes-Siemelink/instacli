package instacli.spec

import instacli.test
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout

class ControlFlow {

    @Test
    fun `do`() {
        test("control-flow/Do tests.cli")
    }

    @Test
    fun `if`() {
        test("control-flow/If tests.cli")
    }

    @Test
    fun forEach() {
        test("control-flow/For each tests.cli")
    }

    @Test
    @Timeout(3)
    fun repeat() {
        test("control-flow/Repeat tests.cli")
    }

    @Test
    fun executeScriptFile() {
        test("control-flow/greet.cli")
        test("control-flow/Run Instacli file tests.cli")
    }
}


