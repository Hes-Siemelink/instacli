package instacli.cli

import org.junit.jupiter.api.Test
import java.io.File

val testDir: File = File("src/test/resources")

class MainTest {

    @Test
    fun `list commands`() {
        // Given
        val session = InstacliInvocation(arrayOf("sample"), workingDir = testDir)

        // When
        session.run()
    }

}