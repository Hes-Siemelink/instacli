package instacli.spec

import instacli.loadTestCases
import org.junit.jupiter.api.DynamicNode
import org.junit.jupiter.api.TestFactory

class UtilityCommandTests {

    @TestFactory
    fun `Cli scripts`(): List<DynamicNode> {
        return loadTestCases("util/Wait tests.cli")
    }
}
