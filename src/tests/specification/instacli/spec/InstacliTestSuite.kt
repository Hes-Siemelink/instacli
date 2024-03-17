package instacli.spec

import instacli.cli.CliFile
import instacli.commands.HttpServer
import instacli.util.TestPrompt
import instacli.util.UserPrompt
import org.junit.jupiter.api.*

class InstacliTestSuite {

    @BeforeEach
    fun setup() {
        UserPrompt.default = TestPrompt
    }

    @TestFactory
    fun `Tests in Command Reference`(): List<DynamicNode> {
        return TestPaths.COMMANDS.getTestCases()
    }

    @TestFactory
    fun `Code examples in Command Reference`(): List<DynamicNode> {
        return TestPaths.COMMANDS.getCodeExamples()
    }

    @TestFactory
    fun `Code examples in Language Spec`(): List<DynamicNode> {
        return TestPaths.LANGUAGE.getCodeExamples()
    }

    @TestFactory
    fun `Tests in Language Spec`(): List<DynamicNode> {
        return TestPaths.LANGUAGE.getTestCases()
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