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

        private val server = TestServer.create()

        @BeforeAll
        @JvmStatic
        fun startTestServer() {
            server.start(25125)
        }

        @AfterAll
        @JvmStatic
        fun stopTestServer() {
            server.stop()
        }
    }
}

