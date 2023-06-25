package yay.samples

import org.junit.jupiter.api.Test
import yay.cli.runYayScript

class DigitalAiApiTests {

    @Test
    fun getInstaller() {
        test("digitalai/platform/cc/get-installer.yay")
    }

    private fun test(resource: String) {
        runYayScript(toFile(resource))
    }
}