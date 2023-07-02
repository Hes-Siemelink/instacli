package instacli.spec

import instacli.script.commands.http.TestServer
import instacli.test
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class Http {

    @Test
    fun httpTests() {
        test("http/HTTP tests.cli")
    }


    companion object {
        @BeforeAll
        @JvmStatic
        fun startTestServer() {
            TestServer.start()
        }

        @AfterAll
        @JvmStatic
        fun stopTestServer() {
            TestServer.stop()
        }
    }
}

