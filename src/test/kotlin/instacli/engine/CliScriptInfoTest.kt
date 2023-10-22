package instacli.engine

import com.fasterxml.jackson.module.kotlin.contains
import instacli.commands.InputInfo
import instacli.util.Yaml
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CliScriptInfoTest {

    @Test
    fun scriptInfo() {
        val script = Yaml.readResource("script-info/greet.cli")
        assertTrue("Script info" in script)

        val scriptInfo = CliScriptInfo.from(script.get("Script info"))
        assertEquals("Creates a greeting", scriptInfo.description)
    }

    @Test
    fun input() {
        val script = Yaml.readResource("script-info/greet.cli")
        assertTrue("Input" in script)

        val input = InputInfo.from(script.get("Input"))

        assertTrue("name" in input.parameters.keys)
        val name = input.parameters["name"]
        assertEquals("The name to greet", name?.description)
        assertEquals("world", name?.default)
        assertEquals("text", name?.tag)

        assertTrue("language" in input.parameters.keys)
        val language = input.parameters["language"]
        assertEquals("en", language?.description)
    }
}

