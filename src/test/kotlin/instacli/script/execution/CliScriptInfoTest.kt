package instacli.script.execution

import com.fasterxml.jackson.module.kotlin.contains
import instacli.util.Yaml
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CliScriptInfoTest {

    @Test
    fun loadScriptInfoInput() {
        val script = Yaml.readResource("script-info/greet.cli")
        assertTrue("Script info" in script)

        val scriptInfo = CliScriptInfo.from(script.get("Script info"))

        assertTrue("name" in scriptInfo.input.keys)
        val name = scriptInfo.input["name"]
        assertEquals("The name to greet", name?.description)
        assertEquals("text", name?.type)
        assertEquals("world", name?.default)

        assertTrue("language" in scriptInfo.input.keys)
        val language = scriptInfo.input["language"]
        assertEquals("en", language?.description)
    }
}

