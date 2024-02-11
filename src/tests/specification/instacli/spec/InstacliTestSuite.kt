package instacli.spec

import instacli.cli.CliFile
import instacli.commands.InternalHttpServer
import instacli.util.TestPrompt
import instacli.util.UserPrompt
import org.junit.jupiter.api.*
import java.nio.file.Path

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
    fun `Code examples in main README`(): List<DynamicNode> {
        return Path.of("README.md").getCodeExamples()
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
            InternalHttpServer.stop()
        }
    }
}