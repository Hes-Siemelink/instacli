package instacli.files

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.module.kotlin.readValue
import instacli.language.CommandInfo
import instacli.util.IO.isTempDir
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
            val instacliYaml = dir.resolve(".instacli.yaml")

            val info = if (instacliYaml.exists()) {
                Yaml.mapper.readValue(instacliYaml.toFile())
            } else {
                DirectoryInfo()
            }

            // Pull description from README.md if not set
            if (info.description.isEmpty()) {
                val readme = dir.resolve("README.md")
                info.description = getDescriptionFromMarkdown(readme)
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

        private fun getDescriptionFromMarkdown(readme: Path): String {
            return if (readme.exists()) {
                val doc = InstacliMarkdown.scan(readme)
                doc.description ?: ""
            } else {
                ""
            }
        }
    }
}

object InstacliDirectories {
    val directories = mutableMapOf<Path, DirectoryInfo>()

    fun get(dir: Path): DirectoryInfo {
        return if (dir.isTempDir()) {
            // Do not cache directories that are made on the fly by scripts
            DirectoryInfo.load(dir)
        } else {
            val key = dir.toAbsolutePath().normalize()
            directories.getOrPut(key) {
                DirectoryInfo.load(dir)
            }
        }
    }

}