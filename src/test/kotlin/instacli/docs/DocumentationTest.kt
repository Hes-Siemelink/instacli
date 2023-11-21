package instacli.docs

import org.junit.jupiter.api.DynamicNode
import org.junit.jupiter.api.TestFactory
import java.io.File

class DocumentationTest {

    @TestFactory
    fun `Test code examples`(): List<DynamicNode> {
        return getCodeExamplesInAllFiles(File("instacli-spec/commands"))
    }
}

