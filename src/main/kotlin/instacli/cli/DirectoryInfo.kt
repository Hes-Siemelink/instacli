package instacli.cli

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.module.kotlin.readValue
import instacli.engine.CommandInfo
import instacli.util.Yaml.mapper
import java.io.File

class DirectoryInfo : CommandInfo {

    var dir: File = File(".")

    override var name: String = ""

    @JsonProperty("Script info")
    override var description: String = ""

    val imports = mutableListOf<String>()
    val connections = mutableMapOf<String, String>()


    companion object {
        fun load(dir: File): DirectoryInfo {
            val infoFile = File(dir, ".instacli.yaml")
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