package instacli.spec

import instacli.loadTestCases
import org.junit.jupiter.api.DynamicNode
import org.junit.jupiter.api.TestFactory

class IOTests {

    @TestFactory
    fun `Files`(): List<DynamicNode> {
        return loadTestCases("files/Read file tests.cli")
    }

    @TestFactory
    fun `Shell`(): List<DynamicNode> {
        return loadTestCases("shell/Shell tests.cli")
    }

}