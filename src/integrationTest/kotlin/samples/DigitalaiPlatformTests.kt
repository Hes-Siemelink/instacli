package samples

import instacli.cli.InstacliMain
import org.junit.jupiter.api.Test

class DigitalaiPlatformTests {

    @Test
    fun listCloudConnectors() {
        test("digitalai/platform/cloud-connector/list-cloud-connectors.cli")
    }

    private fun test(resource: String) {
        InstacliMain("-q", toPath(resource).toString()).run()
    }
}