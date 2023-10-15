package instacli.spec

import instacli.loadTestCases
import org.junit.jupiter.api.DynamicNode
import org.junit.jupiter.api.TestFactory

class FileTests {

    @TestFactory
    fun `Cli scripts`(): List<DynamicNode> {
        return loadTestCases("files/Read file tests.cli")
    }

}