package instacli.script

import instacli.cli.InstacliMain
import instacli.spec.TestPaths
import io.kotest.assertions.throwables.shouldThrow
import org.junit.jupiter.api.Test

class ExceptionTest {

    @Test
    fun `Command format exception`() {

        // Given
        val session = InstacliMain(
            "-q", "exceptions", "command-format-exception.cli",
            workingDir = TestPaths.resources
        )

        // Then
        shouldThrow<CommandFormatException> {

            // When
            session.run()
        }
    }
}

