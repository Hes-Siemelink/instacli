package instacli.samples

import instacli.cli.InstacliInvocation
import org.junit.jupiter.api.Test

class DigitalaiPlatformTests {

    @Test
    fun getInstaller() {
        test("digitalai/platform/cloud-connector/get-installer.cli")
    }

    private fun test(resource: String) {
        InstacliInvocation(arrayOf(toFile(resource).path)).run()
    }
}