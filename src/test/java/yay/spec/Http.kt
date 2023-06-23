package yay.spec

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import yay.commands.http.TestServer
import yay.test

class Http {

    @Test
    fun httpTests() {
        test("http/HTTP tests.yay")
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

