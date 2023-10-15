package instacli.spec

import instacli.script.commands.USER_INPUT_HANDLER
import instacli.test
import instacli.util.MockUser
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UserInputTests {

    @BeforeEach
    fun setup() {
        USER_INPUT_HANDLER = MockUser()
    }

    @Test
    fun `User input tests`() {
        test("input/User input tests.cli")
    }
}