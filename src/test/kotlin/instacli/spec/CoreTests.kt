package instacli.spec

import instacli.loadTestCases
import org.junit.jupiter.api.DynamicNode
import org.junit.jupiter.api.TestFactory

class CoreTests {

    @TestFactory
    fun `Assertions`(): List<DynamicNode> {
        return loadTestCases("assert/Assert statement tests.cli")
    }

    @TestFactory
    fun `Control flow`(): List<DynamicNode> {
        return loadTestCases(
            "control-flow/Do tests.cli",
            "control-flow/Exit tests.cli",
            "control-flow/If tests.cli",
            "control-flow/For each tests.cli",
            "control-flow/Repeat tests.cli",
            "control-flow/greet.cli",
            "control-flow/Run Instacli file tests.cli"
        )
    }

    @TestFactory
    fun `Data manipulation`(): List<DynamicNode> {
        return loadTestCases(
            "data-manipulation/For each and merge.cli",
            "data-manipulation/Merge data.cli",
            "data-manipulation/Object tests.cli",
            "data-manipulation/Replace handler tests.cli",
            "data-manipulation/Size tests.cli",
        )
    }

    @TestFactory
    fun `Util`(): List<DynamicNode> {
        return loadTestCases(
            "util/Wait tests.cli",
            "util/Base64 tests.cli"
        )
    }

    @TestFactory
    fun `Variables`(): List<DynamicNode> {
        return loadTestCases(
            "variables/Result variable tests.cli",
            "variables/Set multiple variables from output.cli",
            "variables/Variable replacement in text.cli"
        )
    }

    @TestFactory
    fun `Eval`(): List<DynamicNode> {
        return loadTestCases(
            "eval/Eval tests.cli",
        )
    }

}