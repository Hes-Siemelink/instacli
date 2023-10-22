package instacli.spec

import instacli.TestServer
import instacli.loadTestCases
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DynamicNode
import org.junit.jupiter.api.TestFactory

class HttpTests {

    @TestFactory
    fun `Cli scripts`(): List<DynamicNode> {
        return loadTestCases("http/HTTP tests.cli")
    }

    companion object {
        @BeforeAll
        @JvmStatic
        fun startTestServer() {
            TestServer.start()
        }

        @AfterAll
        @JvmStatic
        fun stopTestServer() {
            TestServer.stop()
        }
    }
}

