package instacli.cli

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class CliCommandLineOptionsTest {

    @Test
    fun noFlag() {
        assertEquals("home", noFlag("-home"))
        assertEquals("home", noFlag("--home"))
        assertEquals("home", noFlag("---home"))
        assertEquals("home-dir", noFlag("---home-dir"))
        assertEquals("home-dir", noFlag("home-dir"))
    }

    @Test
    fun isFlag() {
        assertTrue(isFlag("-h"))
        assertTrue(isFlag("--home"))
        assertFalse(isFlag("home"))
        assertFalse(isFlag("home-dir"))
    }

    @Test
    fun toParameterMap() {
        assertEquals(
            mapOf(
                "home" to "~",
                "target" to "/tmp"
            ),
            toParameterMap(
                listOf(
                    "--home",
                    "~",
                    "--target",
                    "/tmp"
                )
            )
        )
    }
}