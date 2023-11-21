package instacli.docs

import org.junit.jupiter.api.Test


class DocumentationTest {

    @Test
    fun `Do_md`() {
        val file = getDocFile("control-flow/Do.md")
        validateCodeSnippets(file)
    }

    @Test
    fun `Exit_md`() {
        val file = getDocFile("control-flow/Exit.md")
        validateCodeSnippets(file)
    }

    @Test
    fun `For each_md`() {
        val file = getDocFile("control-flow/For each.md")
        validateCodeSnippets(file)
    }

    @Test
    fun `If_md`() {
        val file = getDocFile("control-flow/If.md")
        validateCodeSnippets(file)
    }

    @Test
    fun `Repeat_md`() {
        val file = getDocFile("control-flow/Repeat.md")
        validateCodeSnippets(file)
    }

    @Test
    fun `When_md`() {
        val file = getDocFile("control-flow/When.md")
        validateCodeSnippets(file)
    }

    @Test
    fun `Assert that_md`() {
        val file = getDocFile("testing/Assert that.md")
        validateCodeSnippets(file)
    }

    @Test
    fun `Assert equals_md`() {
        val file = getDocFile("testing/Assert equals.md")
        validateCodeSnippets(file)
    }
}

