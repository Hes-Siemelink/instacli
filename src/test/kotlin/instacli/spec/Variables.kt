package instacli.spec

import instacli.test
import org.junit.jupiter.api.Test

class Variables {

    @Test
    fun output() {
        test("variables/Result variable tests.cli")
        test("variables/Set multiple variables from output.cli")
    }

    @Test
    fun variableReplacement() {
        test("variables/Variable replacement in text.cli")
    }
}

