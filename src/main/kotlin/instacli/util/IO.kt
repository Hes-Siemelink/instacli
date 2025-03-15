package instacli.util

import java.io.ByteArrayOutputStream
import java.io.PrintStream

object IO {
    fun captureSystemOut(doThis: () -> Unit): String {

        val (originalOut, capturedOut) = rewireSystemOut()

        try {
            doThis()

            capturedOut.flush()
            return capturedOut.toString()
        } finally {
            System.setOut(originalOut)
        }
    }

    fun captureSystemOutAndErr(doThis: () -> Unit): Pair<String, String> {

        val (originalOut, capturedOut) = rewireSystemOut()
        val (originalErr, capturedErr) = rewireSystemErr()

        try {
            doThis()

            capturedOut.flush()
            capturedErr.flush()
            return Pair(capturedOut.toString(), capturedErr.toString())
        } finally {
            System.setOut(originalOut)
            System.setErr(originalErr)
        }
    }

    fun rewireSystemOut(): Pair<PrintStream, ByteArrayOutputStream> {
        val original = System.out
        val capturedOutput = ByteArrayOutputStream()
        System.setOut(PrintStream(capturedOutput))
        return Pair(original, capturedOutput)
    }

    fun rewireSystemErr(): Pair<PrintStream, ByteArrayOutputStream> {
        val original = System.err
        val capturedOutput = ByteArrayOutputStream()
        System.setErr(PrintStream(capturedOutput))
        return Pair(original, capturedOutput)
    }
}