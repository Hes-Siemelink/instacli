package yay.spec

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import yay.test

class Variables {

    @Test
    fun output() {
        test("variables/Result variable tests.yay")
        test("variables/Set multiple variables from output.yay")
    }

    @Test
    fun variableReplacement() {
        test("variables/Variable replacement in text.yay")
    }

    @Test
    @Disabled
    fun rawAndLive() {
        test("variables/Raw and live.yay")
    }
}

