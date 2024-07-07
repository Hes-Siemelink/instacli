package instacli.cli

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.module.kotlin.readValue
import instacli.language.CommandInfo
import instacli.util.Json
import instacli.util.Yaml.mapper
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.name

class DirectoryInfo : CommandInfo {

    var dir: Path = Path.of(".")
    override var hidden: Boolean = false

    override var name: String = ""

    @JsonProperty("Script info")
    override var description: String = ""

    val imports = mutableListOf<String>()
    val connections = Json.newObject()

    var types = Json.newObject()

    companion object {
        fun load(dir: Path): DirectoryInfo {
            val infoFile = dir.resolve(".instacli.yaml")
            val info = if (infoFile.exists()) {
                mapper.readValue(infoFile.toFile())
            } else {
                DirectoryInfo()
            }
            info.dir = dir

            if (info.name.isEmpty()) {
                info.name = dir.name
            }

            val typesFile = dir.resolve("types.yaml")
            if (typesFile.exists()) {
                info.types = mapper.readValue(typesFile.toFile())
            }

            return info
        }
    }
}