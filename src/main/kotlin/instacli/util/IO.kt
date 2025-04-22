package instacli.util

import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.io.PrintStream
import java.nio.file.Path
import java.nio.file.Paths

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
        val copy = ByteArrayOutputStream()
        System.setOut(dualStream(original, copy))
        return Pair(original, copy)
    }

    fun rewireSystemErr(): Pair<PrintStream, ByteArrayOutputStream> {
        val original = System.err
        val copy = ByteArrayOutputStream()
        System.setErr(dualStream(original, copy))
        return Pair(original, copy)
    }

    private fun dualStream(
        original: PrintStream,
        byteArrayOutputStream: ByteArrayOutputStream
    ): PrintStream {
        val customOut = PrintStream(object : OutputStream() {
            override fun write(b: Int) {
                original.write(b) // Write to the console
                byteArrayOutputStream.write(b) // Write to the ByteArrayOutputStream
            }
        })
        return customOut
    }

    val TEMP_DIR: Path = Paths.get(System.getProperty("java.io.tmpdir")).toAbsolutePath().normalize()

    fun Path.isTempDir(): Boolean {
        return toAbsolutePath().normalize().startsWith(TEMP_DIR)
    }
}


