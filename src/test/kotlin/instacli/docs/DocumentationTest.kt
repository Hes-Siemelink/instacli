package instacli.docs

import instacli.TestServer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DynamicNode
import org.junit.jupiter.api.TestFactory
import java.io.File

class DocumentationTest {

    @TestFactory
    fun `Test code examples`(): List<DynamicNode> {
        return getCodeExamplesInAllFiles(File("instacli-spec/command-reference"))
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

