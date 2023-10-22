package instacli.spec

import instacli.commands.USER_INPUT_HANDLER
import instacli.loadTestCases
import instacli.util.MockUser
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DynamicNode
import org.junit.jupiter.api.TestFactory

class UserInputTests {

    @BeforeEach
    fun setup() {
        USER_INPUT_HANDLER = MockUser()
    }

    @TestFactory
    fun `Cli scripts`(): List<DynamicNode> {
        return loadTestCases("input/User input tests.cli")
    }
}