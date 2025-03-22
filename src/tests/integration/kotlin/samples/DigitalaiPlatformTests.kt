package samples

import instacli.cli.InstacliMain
import instacli.commands.connections.Credentials
import instacli.commands.connections.setCredentials
import instacli.files.CliFileContext
import org.junit.jupiter.api.Assumptions
import org.junit.jupiter.api.Test
import kotlin.io.path.exists

class DigitalaiPlatformTests {

    @Test
    fun listCloudConnectors() {
        test("digitalai/platform/cloud-connector/list-cloud-connectors.cli")
    }

    private fun test(resource: String) {
        Assumptions.assumeTrue(
            TestPaths.TEST_CREDENTIALS.exists(),
            "Missing file: ${TestPaths.TEST_CREDENTIALS}"
        )

        val file = toPath(resource)
        val testContext = CliFileContext(file, interactive = false)
        val credentials = Credentials.fromFile(TestPaths.TEST_CREDENTIALS)
        testContext.setCredentials(credentials)

        InstacliMain("-q", file.toString()).run(testContext)
    }
}