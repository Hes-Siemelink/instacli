package instacli.cli

import com.fasterxml.jackson.module.kotlin.readValue
import instacli.util.Yaml.mapper
import java.io.File

class DirectoryInfo {

    val imports = mutableListOf<String>()
    var summary: String = ""

    companion object {
        fun load(dir: File): DirectoryInfo {
            val infoFile = File(dir, "instacli-info.yaml")
            if (infoFile.exists()) {
                return mapper.readValue(infoFile)
            } else {
                return DirectoryInfo()
            }
        }
    }
}