package instacli.spec

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DynamicNode
import org.junit.jupiter.api.TestFactory
import java.nio.file.Path

class DocumentationTestSuite {

    @TestFactory
    fun `Code examples in command-reference`(): List<DynamicNode> {
        return getCodeExamplesInAllFiles(Path.of("instacli-spec/command-reference"))
    }

    @TestFactory
    fun `Code examples in main README`(): List<DynamicNode> {
        return getCodeExamplesInDocument(Path.of("README.md"))
    }

    companion object {

        private val server = InstacliSampleServer.create()

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

