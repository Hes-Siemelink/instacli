package instacli.docs

import instacli.TestServer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DynamicNode
import org.junit.jupiter.api.TestFactory
import java.io.File

class DocumentationTest {

    @TestFactory
    fun `Code examples in command-reference`(): List<DynamicNode> {
        return getCodeExamplesInAllFiles(File("instacli-spec/command-reference"))
    }

    @TestFactory
    @Disabled // FIXME
    fun `Code examples in main README`(): List<DynamicNode> {
        return getCodeExamplesInDocument(File("README.md"))
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

