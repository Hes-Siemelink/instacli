package yay.samples

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import yay.Cli
import yay.toFile

class DigitalAiApiTests {

    @Test
    @Disabled
    fun getInstaller() {
        test("../samples/digitalai-platform-api/get-installer.yay")
    }

    private fun test(resource: String) {
        Cli(toFile(resource)).runScript()
    }
}