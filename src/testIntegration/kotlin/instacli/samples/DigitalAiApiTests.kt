package instacli.samples

import instacli.script.files.CliScriptFile
import org.junit.jupiter.api.Test

class DigitalAiApiTests {

    @Test
    fun getInstaller() {
        test("digitalai/platform/cc/get-installer.cli")
    }

    private fun test(resource: String) {
        CliScriptFile(toFile(resource)).run()
    }
}