package instacli.spec

import instacli.loadTestCases
import org.junit.jupiter.api.DynamicNode
import org.junit.jupiter.api.TestFactory

class ConnectionTests {

    @TestFactory
    fun `Connection management`(): List<DynamicNode> {
        return loadTestCases("connections/Connection tests.cli")
    }
}