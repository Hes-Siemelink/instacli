package instacli.util

import io.kjson.JSON.asInt
import io.kjson.JSONObject
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class JsonWallTest {

    @Test
    fun `test roundtrip`() {
        val json = """
            {"a": 1}
        """.trimIndent()

        val jackson = Yaml.parse(json)
        jackson["a"].intValue() shouldBe 1

        val wall = jackson.toWall() as JSONObject
        wall["a"].asInt shouldBe 1

        val jacksonBack = wall.toJackson()
        jacksonBack["a"].intValue() shouldBe 1
    }
}

