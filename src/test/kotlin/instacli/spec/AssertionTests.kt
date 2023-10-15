package instacli.spec

import instacli.loadTestCases
import org.junit.jupiter.api.DynamicNode
import org.junit.jupiter.api.TestFactory

class AssertionTests {

    @TestFactory
    fun `Cli scripts`(): List<DynamicNode> {
        return loadTestCases("assert/Assert statement tests.cli")
    }
}