package instacli.spec

import instacli.loadTestCases
import org.junit.jupiter.api.DynamicNode
import org.junit.jupiter.api.TestFactory

class VariableTests {

    @TestFactory
    fun `Cli scripts`(): List<DynamicNode> {
        return loadTestCases(
            "variables/Result variable tests.cli",
            "variables/Set multiple variables from output.cli",
            "variables/Variable replacement in text.cli"
        )
    }
//    @Test
//    fun output() {
//        test("variables/Result variable tests.cli")
//        test("variables/Set multiple variables from output.cli")
//    }
//
//    @Test
//    fun variableReplacement() {
//        test("variables/Variable replacement in text.cli")
//    }
}

