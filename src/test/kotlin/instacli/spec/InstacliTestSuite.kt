package instacli.spec

import instacli.commands.userPrompt
import instacli.util.MockUser
import org.junit.jupiter.api.*
import java.io.File

class InstacliTestSuite {

    @BeforeEach
    fun setup() {
        userPrompt = MockUser()
    }

    @TestFactory
    fun `Instacli tests in test-suite`(): List<DynamicNode> {
        return getAllInstacliTests(File("instacli-spec/test-suite"))
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