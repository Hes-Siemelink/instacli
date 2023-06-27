package instacli.samples

import instacli.cli.runCliScriptFile
import org.junit.jupiter.api.Test

class DigitalAiApiTests {

    @Test
    fun getInstaller() {
        test("digitalai/platform/cc/get-installer.cli")
    }

    private fun test(resource: String) {
        runCliScriptFile(toFile(resource))
    }
}