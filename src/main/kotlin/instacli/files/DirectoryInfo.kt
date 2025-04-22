package instacli.files

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.module.kotlin.readValue
import instacli.files.MarkdownBlock.YamlInstacli
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
            val readme = dir.resolve("README.md")
            val instacliYaml = dir.resolve(".instacli.yaml")

            val info = if (readme.exists()) {
                val doc = InstacliMarkdown.scan(readme)
                val yaml = doc.blocks.filter { it.type == YamlInstacli }
                if (yaml.isEmpty()) {
                    DirectoryInfo().apply {
                        description = doc.description ?: ""
                    }
                } else {
                    val content = yaml.joinToString("\n") { it.getContent() }
                    Yaml.mapper.readValue(content)
                }

            } else if (instacliYaml.exists()) {
                Yaml.mapper.readValue(instacliYaml.toFile())
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