package instacli.spec

import instacli.test
import org.junit.jupiter.api.Test

class FileTests {

    @Test
    fun readFiles() {
        test("files/Read file tests.cli")
    }
}