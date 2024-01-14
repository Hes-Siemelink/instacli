package instacli.script

import com.fasterxml.jackson.module.kotlin.contains
import instacli.commands.InputData
import instacli.commands.ScriptInfoData
import instacli.spec.TestPaths
import instacli.util.Yaml
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CliScriptInfoTest {

    @Test
    fun scriptInfo() {
        val script = Yaml.readFile(TestPaths.RESOURCES.resolve("script-info/greet.cli"))
        assertTrue("Script info" in script)

        val scriptInfo = ScriptInfoData.from(script.get("Script info"))
        assertEquals("Creates a greeting", scriptInfo.description)
    }

    @Test
    fun input() {
        val script = Yaml.readFile(TestPaths.RESOURCES.resolve("script-info/greet.cli"))
        assertTrue("Script info" in script)

        val input = InputData.from(script.get("Script info").get("input"))

        assertTrue("name" in input.parameters.keys)
        val name = input.parameters["name"]
        assertEquals("The name to greet", name?.description)
        assertEquals("world", name?.default?.asText())
        assertEquals("text", name?.type)

        assertTrue("language" in input.parameters.keys)
        val language = input.parameters["language"]
        assertEquals("en", language?.description)
    }
}

