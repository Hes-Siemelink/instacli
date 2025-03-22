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
    fun `Tests in Instacli spec`(): List<DynamicNode> {
        return TestPaths.SPEC.getTestCases()
    }

    @TestFactory
    fun `Code examples in Instacli spec`(): List<DynamicNode> {
        return TestPaths.SPEC.getCodeExamples()
    }

    @TestFactory
    fun `Code examples in main README`(): List<DynamicNode> {
        return TestPaths.README.getCodeExamples()
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