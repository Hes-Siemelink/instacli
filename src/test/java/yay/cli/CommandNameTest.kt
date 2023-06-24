package yay.cli

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CommandNameTest {

    @Test
    fun toYayCommand() {
        assertEquals("Command", asYayCommand("command"))
        assertEquals("Command", asYayCommand("command.yay"))
        assertEquals("Command with words", asYayCommand("command-with-words.yay"))
        assertEquals("Command with words", asYayCommand("Command with words"))
        assertEquals("Command with MIXED Case", asYayCommand("Command with MIXED Case"))
    }

    @Test
    fun toCliCommand() {
        assertEquals("command", asCliCommand("command"))
        assertEquals("command", asCliCommand("command.yay"))
        assertEquals("command-with-words", asCliCommand("command-with-words.yay"))
        assertEquals("command-with-words", asCliCommand("Command with words"))
        assertEquals("command-with-mixed-case", asCliCommand("Command with MIXED Case"))
    }
}