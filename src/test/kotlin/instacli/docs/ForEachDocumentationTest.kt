package instacli.docs

import org.junit.jupiter.api.Test


class ForEachDocumentationTest {

    @Test
    fun `Validate code snippets in For each doc`() {
        val file = getDocFile("For each.md")
        validateCodeSnippets(file)
    }
}

