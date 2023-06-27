package instacli.samples

import instacli.cli.runCliScript
import org.junit.jupiter.api.Test

class DigitalAiApiTests {

    @Test
    fun getInstaller() {
        test("digitalai/platform/cc/get-installer.cli")
    }

    private fun test(resource: String) {
        runCliScript(toFile(resource))
    }
}