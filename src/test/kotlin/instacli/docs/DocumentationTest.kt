package instacli.docs

import org.junit.jupiter.api.Test


class DocumentationTest {

    @Test
    fun `Do_md`() {
        val file = getDocFile("Do.md")
        validateCodeSnippets(file)
    }

    @Test
    fun `Exit_md`() {
        val file = getDocFile("Exit.md")
        validateCodeSnippets(file)
    }

    @Test
    fun `For each_md`() {
        val file = getDocFile("For each.md")
        validateCodeSnippets(file)
    }

    @Test
    fun `If_md`() {
        val file = getDocFile("If.md")
        validateCodeSnippets(file)
    }

    @Test
    fun `When_md`() {
        val file = getDocFile("When.md")
        validateCodeSnippets(file)
    }
}

