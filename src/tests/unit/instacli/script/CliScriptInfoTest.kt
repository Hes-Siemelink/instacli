package instacli.script

import com.fasterxml.jackson.module.kotlin.contains
import instacli.TestPaths
import instacli.commands.InputData
import instacli.commands.ScriptInfoData
import instacli.util.Yaml
import instacli.util.toDomainObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CliScriptInfoTest {

    @Test
    fun scriptInfo() {
        val script = Yaml.readFile(TestPaths.RESOURCES.resolve("script-info/greet.cli"))
        assertTrue("Script info" in script)

        val scriptInfo = script.get("Script info").toDomainObject(ScriptInfoData::class)
        assertEquals("Creates a greeting", scriptInfo.description)
    }

    @Test
    fun input() {
        val script = Yaml.readFile(TestPaths.RESOURCES.resolve("script-info/greet.cli"))
        assertTrue("Script info" in script)

        val input = script.get("Script info").get("input").toDomainObject(InputData::class)

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

