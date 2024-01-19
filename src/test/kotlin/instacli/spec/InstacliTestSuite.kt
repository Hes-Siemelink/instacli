package instacli.spec

import instacli.commands.userPrompt
import instacli.util.MockUser
import org.junit.jupiter.api.*
import java.nio.file.Path

class InstacliTestSuite {

    @BeforeEach
    fun setup() {
        userPrompt = MockUser()
    }

    @TestFactory
    fun `Instacli reference tests`(): List<DynamicNode> {
        return Path.of("instacli-spec/reference").getTestCases()
    }

    @TestFactory
    fun `Code examples in reference documentation`(): List<DynamicNode> {
        return Path.of("instacli-spec/reference").getCodeExamples()
    }

    @TestFactory
    fun `Code examples in main README`(): List<DynamicNode> {
        return Path.of("README.md").getCodeExamples()
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