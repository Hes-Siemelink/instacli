package instacli.util

import java.io.FileNotFoundException
import java.io.InputStream
import java.net.URI

fun resourceExists(classpathResource: String): Boolean {
    return object {}.javaClass.getResource("/$classpathResource") != null
}

fun getResource(classpathResource: String): URI {
    return object {}.javaClass.getResource("/$classpathResource")?.toURI()
        ?: throw FileNotFoundException("Classpath resource not found: $classpathResource")
}

fun getResourceAsStream(classpathResource: String): InputStream {
    return object {}.javaClass.getResourceAsStream("/$classpathResource")
        ?: throw FileNotFoundException("Classpath resource not found: $classpathResource")
}
