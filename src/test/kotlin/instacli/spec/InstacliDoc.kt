package instacli.spec

import java.nio.file.Path
import kotlin.io.path.readLines

const val START_RUN_BEFORE_CODE_EXAMPLE = "<!-- run before"
const val END_RUN_BEFORE_CODE_EXAMPLE = "-->"
const val START_CODE_EXAMPLE = "```yaml"
const val END_CODE_EXAMPLE = "```"

val FILE_REGEX = Regex("file:(\\S+)")

class InstacliDoc(val docFile: Path) {

    val codeExamples = mutableListOf<String>()
    val helperFiles = mutableMapOf<String, String>()

    fun scan() {

        var currentSnippet = mutableListOf<String>()
        var currentFile: String? = null
        var recording = false
        for (line in docFile.readLines()) {
            if (recording) {
                when {
                    line.startsWith(END_RUN_BEFORE_CODE_EXAMPLE) -> {
                        recording = false
                    }

                    line.startsWith(END_CODE_EXAMPLE) -> {
                        val content = currentSnippet.joinToString("\n")
                        if (currentFile == null) {
                            codeExamples.add(content)
                        } else {
                            helperFiles[currentFile] = content
                        }
                        currentSnippet = mutableListOf()
                        recording = false
                    }

                    else -> {
                        currentSnippet.add(line)
                    }
                }
            } else {
                if (line.startsWith(START_RUN_BEFORE_CODE_EXAMPLE)) {
                    recording = true
                }
                if (line.startsWith(START_CODE_EXAMPLE)) {
                    recording = true
                    val fileMatch = FILE_REGEX.find(line)
                    currentFile = fileMatch?.groupValues?.get(1)
                }
            }
        }
    }
}