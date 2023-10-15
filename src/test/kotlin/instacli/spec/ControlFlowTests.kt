package instacli.spec

import instacli.test
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout

class ControlFlowTests {

    @Test
    fun `Do`() {
        test("control-flow/Do tests.cli")
    }

    @Test
    fun `If`() {
        test("control-flow/If tests.cli")
    }

    @Test
    fun `For each`() {
        test("control-flow/For each tests.cli")
    }

    @Test
    @Timeout(3)
    fun `Repeat`() {
        test("control-flow/Repeat tests.cli")
    }

    @Test
    fun `Run script`() {
        test("control-flow/greet.cli")
        test("control-flow/Run Instacli file tests.cli")
    }
}


