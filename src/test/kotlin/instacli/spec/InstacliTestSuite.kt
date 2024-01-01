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
    fun `Instacli tests in test-suite`(): List<DynamicNode> {
        return getAllInstacliTests(Path.of("instacli-spec/test-suite"))
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