package instacli.spec

import instacli.commands.http.HttpServer
import instacli.commands.userinteraction.TestPrompt
import instacli.commands.userinteraction.UserPrompt
import instacli.files.CliFile
import org.junit.jupiter.api.*

class InstacliTestSuite {

    @BeforeEach
    fun setup() {
        UserPrompt.default = TestPrompt
    }

    @TestFactory
    fun `Main README_md`(): List<DynamicNode> {
        return CliFile(TestPaths.README).getCodeExamples()
    }

    @TestFactory
    fun `instacli-spec`(): List<DynamicNode> {
        return TestPaths.SPEC.getTests()
    }

    companion object {

        @BeforeAll
        @JvmStatic
        fun startTestServer() {
            CliFile(TestPaths.SAMPLE_SERVER).run()
        }

        @AfterAll
        @JvmStatic
        fun stopTestServer() {
            HttpServer.stop(2525)
        }
    }
}