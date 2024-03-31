package instacli.util

import instacli.TestPaths
import instacli.language.CommandFormatException
import io.kotest.matchers.shouldBe
import net.pwall.json.JSONValue
import net.pwall.json.schema.JSONSchema
import net.pwall.json.schema.output.BasicOutput
import net.pwall.yaml.YAMLNode
import net.pwall.yaml.YAMLSimple
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.nio.file.Path

class JsonSchemasTest {

    @Test
    fun `Validate Replace (Jackson)`() {
        val yaml = Yaml.readFile(example("Replace.yaml"))

        yaml.validateWithSchema("instacli/data-manipulation/Replace.schema")
    }

    //    @Test
    fun `Validate Replace (Wall)`() {
        val yaml = Wall.readYaml(example("Replace.yaml"))

        val output = Wall.validate(yaml, "Replace.schema.yaml")

        output.valid shouldBe true
    }

    //    @Test
    fun `Validate Equals (Wall)`() {
        val yaml = Wall.readYaml(example("Equals.yaml"))
        val output = Wall.validate(yaml, "Equals.schema.yaml")

        output.valid shouldBe true
    }

    //    @Test
    fun `Validate Equals - incorrect (Wall)`() {
        val yaml = Wall.readYaml(example("Equals-incorrect.yaml"))
        val output = Wall.validate(yaml, "Equals.schema.yaml")

        output.valid shouldBe false
    }

    @Test
    fun `Validate Equals with conditions schema (Jackson)`() {
        val yaml = Yaml.readFile(example("Equals.yaml"))

        yaml.validateWithSchema("instacli/Conditions.schema")
    }

    @Test
    fun `Validate incorrect Equals with conditions schema (Jackson)`() {
        val yaml = Yaml.readFile(example("Equals-incorrect.yaml"))

        assertThrows<CommandFormatException> {
            yaml.validateWithSchema("instacli/Conditions.schema")
        }
    }

    //    @Test
    fun `Validate Equals with conditions schema (Wall)`() {
        val yaml = Wall.readYaml(example("Equals.yaml"))
        val output = Wall.validate(yaml, "Conditions.schema.yaml")

        output.valid shouldBe true
    }

    //    @Test
    fun `Validate incorrect Equals with conditions schema (Wall)`() {
        val yaml = Wall.readYaml(example("Equals-incorrect.yaml"))
        val output = Wall.validate(yaml, "Conditions.schema.yaml")

        output.valid shouldBe false
    }

    @Test
    fun `Validate nested conditions (Jackson)`() {
        val yaml = Yaml.readFile(example("Nested conditions.yaml"))

        yaml.validateWithSchema("instacli/Conditions.schema")
    }

    //    @Test
    fun `Validate nested conditions (Wall)`() {
        val yaml = Wall.readYaml(example("Nested conditions.yaml"))
        val output = Wall.validate(yaml, "Conditions.schema.yaml")

        output.valid shouldBe true
    }

    @Test
    fun `Validate nested conditions that are incorrect (Jackson)`() {
        val yaml = Yaml.readFile(example("Nested conditions-incorrect.yaml"))

        assertThrows<CommandFormatException> {
            yaml.validateWithSchema("instacli/Conditions.schema")
        }.let { print(it) }
    }

    //    @Test
//    @Disabled // Bug in json-kotlin-schema?
    fun `Validate nested conditions that are incorrect (Wall)`() {
        val yaml = Wall.readYaml(example("Nested conditions-incorrect.yaml"))
        val output = Wall.validate(yaml, "Conditions.schema.yaml")

        output.valid shouldBe false
    }

    @Test
    fun `Validate with Assert that schema (Jackson)`() {
        val yaml = Yaml.readFile(example("Nested conditions.yaml"))

        yaml.validateWithSchema("instacli/Conditions.schema")
    }

    //    @Test
    fun `Validate with Assert that schema (Wall)`() {
        val yaml = Wall.readYaml(example("Nested conditions.yaml"))
        val output = Wall.validate(yaml, "Assert that.schema.yaml")

        output.valid shouldBe true
    }

    @Test
    fun `Validate incorrect data with Assert that schema (Jackson)`() {
        val yaml = Yaml.readFile(example("Nested conditions-incorrect.yaml"))

        assertThrows<CommandFormatException> {
            yaml.validateWithSchema("instacli/Conditions.schema")
        }.let { print(it.toString().replace(',', '\n')) }
    }

    //    @Test
//    @Disabled // Bug in json-kotlin-schema?
    fun `Validate incorrect data with Assert that schema (Wall)`() {
        val yaml = Wall.readYaml(example("Nested conditions-incorrect.yaml"))
        val output = Wall.validate(yaml, "Assert that.schema.yaml")

        output.valid shouldBe false
    }

}

//
// Helper methods
//

fun example(file: String): Path {
    return TestPaths.RESOURCES.resolve("schema/example/$file")
}

object Wall {
    fun readYaml(file: Path): YAMLNode {
        val yamlDocument = YAMLSimple.process(file.toFile())
        return yamlDocument.rootNode
    }

    fun validate(json: JSONValue, schemaFile: String): BasicOutput {
        val schema = JSONSchema.parseFile("src/main/resources/commands/instacli/$schemaFile")
        val output = schema.validateBasic(json)
        output.errors?.forEach {
            println("${it.error} - ${it.instanceLocation}")
        }
        return output
    }
}