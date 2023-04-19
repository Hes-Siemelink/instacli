package yay

import org.junit.jupiter.api.Test

class DigitalAiApiTests {

    @Test
    fun getAgents() {
        test("/digitalai-platform-api/get-agents.yay")
    }

    private fun test(resource: String) {
        Cli(toFile(resource)).runScript()
    }
}