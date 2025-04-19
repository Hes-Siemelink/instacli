package instacli.spec

import instacli.files.InstacliMarkdown
import instacli.util.IO
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.nio.file.Path

val REFERENCE_DOC: Path = TestPaths.SPEC.resolve("language/Writing Instacli documentation.spec.md")

class DocTest {

    private val doc = InstacliMarkdown.scan(REFERENCE_DOC)

    @Test
    fun `Yaml files`() {
        doc.helperFiles.size shouldBe 1
        doc.helperFiles.keys shouldContain "data.yaml"
        doc.helperFiles["data.yaml"] shouldBe "key: value"
    }

    @Test
    fun `capture println`() {
        val string = IO.captureSystemOut {
            println("Boo!")
        }
        string shouldBe "Boo!\n"
    }
}
