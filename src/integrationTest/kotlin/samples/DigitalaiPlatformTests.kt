package samples

import instacli.cli.InstacliInvocation
import org.junit.jupiter.api.Test

class DigitalaiPlatformTests {

    @Test
    fun listCloudConnectors() {
        test("digitalai/platform/cloud-connector/list-cloud-connectors.cli")
    }

    private fun test(resource: String) {
        InstacliInvocation("-q", toPath(resource).toString()).invoke()
    }
}