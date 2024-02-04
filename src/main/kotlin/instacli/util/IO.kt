package instacli.util

import java.io.ByteArrayOutputStream
import java.io.PrintStream

object IO {
    fun captureSystemOut(doThis: () -> Unit): String {

        // Rewire System,out
        val old = System.out
        val capturedOutput = ByteArrayOutputStream()
        System.setOut(PrintStream(capturedOutput))

        try {
            // Execute code
            doThis()

            return capturedOutput.toString()
        } finally {
            // Restore System.out
            System.out.flush()
            System.setOut(old)
        }
    }
}