package instacli.spec

import specscript.commands.http.HttpServer
import specscript.commands.userinteraction.TestPrompt
import specscript.commands.userinteraction.UserPrompt
import specscript.files.CliFile
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