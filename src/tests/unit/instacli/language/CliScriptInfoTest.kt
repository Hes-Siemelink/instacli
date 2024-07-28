package instacli.language

import com.fasterxml.jackson.module.kotlin.contains
import instacli.TestPaths
import instacli.commands.scriptinfo.ScriptInfoData
import instacli.language.types.ParameterData
import instacli.language.types.TypeDefinition
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

        val input = script.get("Script info").get("input").toDomainObject(TypeDefinition::class)
        input.properties ?: throw CommandFormatException("Missing properties on Script info input ")

        val parameters = input.properties.parameters

        assertTrue("name" in parameters.keys)
        val name = parameters["name"]?.toDomainObject(ParameterData::class)
        assertEquals("The name to greet", name?.description)
        assertEquals("world", name?.default?.asText())
        assertEquals("text", name?.type?.name)

        assertTrue("language" in parameters.keys)
        val language = parameters["language"]?.toDomainObject(ParameterData::class)
        assertEquals("en", language?.description)
    }
}

