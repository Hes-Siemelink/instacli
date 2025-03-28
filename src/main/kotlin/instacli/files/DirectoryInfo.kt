package instacli.files

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.module.kotlin.readValue
import instacli.language.CommandInfo
import instacli.util.Json
import instacli.util.Yaml
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.name

class DirectoryInfo : CommandInfo {

    var dir: Path = Path.of(".")

    override var hidden: Boolean = false
    override var name: String = ""

    @JsonProperty("Script info")
    override var description: String = ""

    @JsonProperty("instacli-spec")
    override var instacliSpec: String = "unknown"

    val imports = mutableListOf<String>()
    val connections = Json.newObject()

    var types = Json.newObject()

    companion object {
        fun load(dir: Path): DirectoryInfo {
            val infoFile = dir.resolve(".instacli.yaml")
            val info = if (infoFile.exists()) {
                Yaml.mapper.readValue(infoFile.toFile())
            } else {
                DirectoryInfo()
            }
            info.dir = dir

            if (info.name.isEmpty()) {
                info.name = dir.name
            }

            val typesFile = dir.resolve("types.yaml")
            if (typesFile.exists()) {
                info.types = Yaml.mapper.readValue(typesFile.toFile())
            }

            return info
        }
    }
}

object InstacliDirectories {
    val directories = mutableMapOf<Path, DirectoryInfo>()

    fun get(dir: Path): DirectoryInfo {
        val key = dir.toAbsolutePath().normalize()
        return directories.getOrPut(key) {
            DirectoryInfo.load(dir)
        }
    }
}