package instacli.util

import java.io.FileNotFoundException
import java.io.InputStream
import java.net.URI

object Resources {
    fun exists(classpathResource: String): Boolean {
        return object {}.javaClass.getResource("/$classpathResource") != null
    }

    fun getUri(classpathResource: String): URI {
        return object {}.javaClass.getResource("/$classpathResource")?.toURI()
            ?: throw FileNotFoundException("Classpath resource not found: $classpathResource")
    }

    fun stream(classpathResource: String): InputStream {
        return object {}.javaClass.getResourceAsStream("/$classpathResource")
            ?: throw FileNotFoundException("Classpath resource not found: $classpathResource")
    }
}
