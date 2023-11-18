package instacli.script

import instacli.cli.InstacliInvocation
import instacli.cli.testDir
import io.kotest.assertions.throwables.shouldThrow
import org.junit.jupiter.api.Test

class ExceptionTest {

    @Test
    fun `Command format exception`() {

        // Given
        val session = InstacliInvocation(arrayOf("-q", "exceptions", "command-format-exception.cli"), workingDir = testDir)

        // Then
        shouldThrow<CommandFormatException> {

            // When
            session.invoke()
        }
    }
}
