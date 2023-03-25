package hes.yak

import com.fasterxml.jackson.databind.node.TextNode
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class VariablesTest {

    val variables = mapOf(
        "1" to TextNode("one"),
        "2" to TextNode("two")
    )

    @Test
    fun replaceInText() {
        Assertions.assertEquals("One", resolveVariablesInText("One", variables))
        Assertions.assertEquals("There is one", resolveVariablesInText("There is \${1}", variables))
        Assertions.assertEquals("There is one and one", resolveVariablesInText("There is \${1} and \${1}", variables))
        Assertions.assertEquals("There is one and one and two", resolveVariablesInText("There is \${1} and \${1} and \${2}", variables))
    }

    @Test
    fun missingVariable() {
        assertThrows<ScriptException> {
            resolveVariablesInText("There is no \${3}", variables)
        }
    }
}