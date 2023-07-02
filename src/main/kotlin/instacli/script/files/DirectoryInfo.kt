package instacli.script.files

import com.fasterxml.jackson.module.kotlin.readValue
import instacli.script.execution.CommandInfo
import instacli.util.Yaml.mapper
import java.io.File

class DirectoryInfo : CommandInfo {

    var dir: File = File(".")

    override var name: String = ""
    override var summary: String = ""
    val imports = mutableListOf<String>()

    companion object {
        fun load(dir: File): DirectoryInfo {
            val infoFile = File(dir, "instacli-info.yaml")
            val info = if (infoFile.exists()) {
                mapper.readValue(infoFile)
            } else {
                DirectoryInfo()
            }
            info.dir = dir

            if (info.name.isEmpty()) {
                info.name = dir.name
            }

            return info
        }
    }
}