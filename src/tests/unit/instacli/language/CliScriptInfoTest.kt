package instacli.language

import com.fasterxml.jackson.module.kotlin.contains
import instacli.TestPaths
import instacli.commands.scriptinfo.ScriptInfoData
import instacli.util.Yaml
import instacli.util.toDomainObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CliScriptInfoTest {

    @Test
    fun scriptInfo() {
        val script = Yaml.readFile(TestPaths.RESOURCES.resolve("script-info/script-info-sample.cli"))
        assertTrue("Script info" in script)

        val scriptInfo = script.get("Script info").toDomainObject(ScriptInfoData::class)
        assertEquals("Creates a greeting", scriptInfo.description)
    }

    @Test
    fun input() {
        val script = Yaml.readFile(TestPaths.RESOURCES.resolve("script-info/script-info-sample.cli"))
        assertTrue("Script info" in script)

        val scriptInfo = script.get("Script info").toDomainObject(ScriptInfoData::class)

        val parameters = scriptInfo.input ?: throw CommandFormatException("Missing Script info input ")

        assertTrue("name" in parameters.keys)
        val name = parameters["name"]!!
        assertEquals("The name to greet", name.description)
        assertEquals("world", name.default?.asText())
        assertEquals("text", name.type?.name)

        assertTrue("language" in parameters.keys)
        val language = parameters["language"]!!
        assertEquals("en", language.description)
    }
}

