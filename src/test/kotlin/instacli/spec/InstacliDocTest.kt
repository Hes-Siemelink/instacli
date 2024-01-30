package instacli.spec

import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.junit.jupiter.api.Test
import java.nio.file.Path

val REFERENCE_DOC: Path = TestPaths.SPEC.resolve("reference/documentation/writing-instacli-docs.md")

class InstacliDocTest {

    private val doc = InstacliDoc.scan(REFERENCE_DOC)

    @Test
    fun `Code examples`() {
        doc.codeExamples.size shouldBe 4
        doc.codeExamples[0] shouldContain "Code example: An Instacli snippet inside Markdown"
        doc.codeExamples[2] shouldContain "Stock answers:"
    }

    @Test
    fun `Yaml files`() {
        doc.helperFiles.size shouldBe 1
        doc.helperFiles.keys shouldContain "data.yaml"
        doc.helperFiles["data.yaml"] shouldBe "key: value"
    }

}
