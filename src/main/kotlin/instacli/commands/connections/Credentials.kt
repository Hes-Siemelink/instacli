package instacli.commands.connections

import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import instacli.cli.InstacliPaths
import instacli.language.ScriptContext
import instacli.util.Json
import instacli.util.Yaml
import instacli.util.toDomainObject
import java.nio.file.Path
import kotlin.io.path.createFile
import kotlin.io.path.exists

//
// Data model
//

class Credentials {

    @JsonAnySetter
    var targetResources: MutableMap<String, TargetResource> = mutableMapOf()

    @JsonIgnore
    var file: Path? = null

    fun save() {
        checkNotNull(file) { "Can't save Credentials object because there is no file associated with it." }

        Yaml.mapper.writeValue(file?.toFile(), this.targetResources)
    }

    fun storeIn(context: ScriptContext) {
        store(context, this)
    }

    companion object {

        const val FILE_NAME = "credentials.yaml"
        private val CONNECTIONS_YAML: Path = InstacliPaths.INSTACLI_HOME.resolve(FILE_NAME)

        private fun store(context: ScriptContext, value: Credentials) {
            context.session[FILE_NAME] = value
        }

        fun getFrom(context: ScriptContext): Credentials {
            return context.session.getOrPut(FILE_NAME) {
                load()
            } as Credentials
        }

        fun load(file: Path = CONNECTIONS_YAML): Credentials {

            createIfNotExists(file)

            val node = Yaml.readFile(file)
            val credentials = node.toDomainObject(Credentials::class)
            credentials.file = file

            return credentials
        }

        private fun createIfNotExists(file: Path = CONNECTIONS_YAML) {
            if (file.exists()) {
                return
            }
            file.createFile()
            val credentials = Credentials()
            credentials.file = file
            credentials.save()
        }
    }
}

class TargetResource {
    var credentials: MutableList<ObjectNode> = mutableListOf()
    var default: String? = null

    fun default(): ObjectNode? {
        return credentials.find { it["name"]?.textValue() == default }
    }

    fun credentials(): ArrayNode {
        val list = ArrayNode(JsonNodeFactory.instance)
        for (item in credentials) {
            list.add(item)
        }
        return list
    }
}

class CreateCredentialsInfo {
    var target: String = "Default"
    var credentials: ObjectNode = Json.newObject()
}
