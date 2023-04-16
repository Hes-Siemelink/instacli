package hes.yay

import hes.yay.http.TestServer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class HttpTests {

    @Test
    fun httpTests() {
        test("HTTP tests.yay")
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

