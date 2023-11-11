package instacli.spec

import instacli.commands.userPrompt
import instacli.loadTestCases
import instacli.util.MockUser
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DynamicNode
import org.junit.jupiter.api.TestFactory

class UserInputTests {

    @BeforeEach
    fun setup() {
        userPrompt = MockUser()
    }

    @TestFactory
    fun `Cli scripts`(): List<DynamicNode> {
        return loadTestCases("input/User input tests.cli")
    }

    @TestFactory
    fun `Connections`(): List<DynamicNode> {
        return loadTestCases("input/Connections.cli")
    }
}