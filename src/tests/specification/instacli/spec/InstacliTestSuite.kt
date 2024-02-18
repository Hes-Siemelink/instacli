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
    fun `Instacli reference tests`(): List<DynamicNode> {
        return TestPaths.REFERENCE.getTestCases()
    }

    @TestFactory
    fun `Code examples in reference documentation`(): List<DynamicNode> {
        return TestPaths.REFERENCE.getCodeExamples()
    }

    @TestFactory
    fun `Code examples in core concepts documentation`(): List<DynamicNode> {
        return TestPaths.CORE_CONCEPTS.getCodeExamples()
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