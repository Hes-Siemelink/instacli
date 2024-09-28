package instacli.commands.db

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class StoreTest {
    
    @Test
    fun expandJsonColumns2() {
        val text = "select $.a where $.b"

        val result = text.expandJsonColumns()

        result shouldBe "select json_extract(json, '$.a') where json_extract(json, '$.b')"
    }
}