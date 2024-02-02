package samples

import instacli.cli.CliFileContext
import instacli.cli.InstacliMain
import instacli.commands.Connections
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
            TestPaths.TEST_CONNECTIONS.exists(),
            "Missing file: ${TestPaths.TEST_CONNECTIONS}"
        )

        val file = toPath(resource)
        val testContext = CliFileContext(file, interactive = false)
        Connections.load(TestPaths.TEST_CONNECTIONS).storeIn(testContext)
        InstacliMain("-q", file.toString()).run(testContext)
    }
}