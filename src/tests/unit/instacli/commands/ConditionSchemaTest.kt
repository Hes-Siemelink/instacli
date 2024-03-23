package instacli.commands

import instacli.TestPaths
import instacli.util.JsonSchemas
import instacli.util.Yaml
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class ConditionSchemaTest {

    @Test
    fun `validate equals`() {
        val condition = """
            item: a
            equals: b
        """.trimIndent()

        validate(condition, "Equals.schema.yaml")
    }

    fun validate(json: String, schema: String) {

        val node = Yaml.parse(json)
        val schema = JsonSchemas.load(TestPaths.SCHEMAS.resolve(schema)) ?: error("Schema not found")
        val messages = schema.validate(node)
        messages.forEach {
            println(it)
        }
        messages.size shouldBe 0
    }
}