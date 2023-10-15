package instacli.spec

import instacli.toDynamicTests
import org.junit.jupiter.api.DynamicNode
import org.junit.jupiter.api.TestFactory

class ControlFlowTests {

    @TestFactory
    fun `Cli scripts`(): List<DynamicNode> {
        return toDynamicTests(
            "control-flow/Do tests.cli",
            "control-flow/If tests.cli",
            "control-flow/For each tests.cli",
            "control-flow/Repeat tests.cli",
            "control-flow/greet.cli",
            "control-flow/Run Instacli file tests.cli"
        )
    }
}

