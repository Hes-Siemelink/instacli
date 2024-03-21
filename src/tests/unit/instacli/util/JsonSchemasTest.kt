package instacli.util

import instacli.TestPaths
import io.kotest.matchers.shouldBe
import net.pwall.json.schema.JSONSchema
import net.pwall.yaml.YAMLSimple
import org.junit.jupiter.api.Test

class JsonSchemasTest {

    @Test
    fun `Validate Replace with old schema validator`() {
        val yaml = Yaml.readFile(TestPaths.RESOURCES.resolve("schema/example/Replace.yaml"))

        yaml.validateWithSchema("Replace")
    }

    @Test
    fun `Validate Replace with new schema validator`() {
        val schema = JSONSchema.parseFile("src/main/resources/schema/Replace.schema.json")
        val file = TestPaths.RESOURCES.resolve("schema/example/Replace.yaml").toFile()
        val yamlDocument = YAMLSimple.process(file)
        val output = schema.validateBasic(yamlDocument.rootNode)
        output.errors?.forEach {
            println("${it.error} - ${it.instanceLocation}")
        }
        output.valid shouldBe true
    }
}