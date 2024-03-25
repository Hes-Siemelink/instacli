package instacli.commands

import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.*
import instacli.cli.CliFile
import instacli.cli.CliFileContext
import instacli.cli.InstacliPaths
import instacli.language.*
import instacli.util.Json
import instacli.util.Yaml
import instacli.util.toDomainObject
import java.nio.file.Path
import kotlin.io.path.createFile
import kotlin.io.path.exists
import kotlin.io.path.name

object GetCredentials : CommandHandler("Get credentials"), ValueHandler {
    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {
        val targetName = data.asText() ?: throw CommandFormatException("Specify target resource")

        val credentials = Credentials.getFrom(context)
        val target = credentials.targetResources[targetName] ?: return TextNode("")
        return when {
            target.default != null -> {
                target.default()
            }

            target.credentials.isNotEmpty() -> {
                target.credentials.first()
            }

            else -> throw InstacliCommandError(
                "No accounts defined for $targetName",
                "no accounts",
                Json.newObject("target", targetName)
            )
        }
    }
}


object CreateCredentials : CommandHandler("Create credentials"), ObjectHandler {
    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode {
        val newCredentials = data.toDomainObject(CreateCredentialsInfo::class)
        val credentials = Credentials.getFrom(context)
        val target = credentials.targetResources.getOrPut(newCredentials.target) {
            TargetResource()
        }

        target.credentials.add(newCredentials.credentials)

        credentials.save()

        return newCredentials.credentials
    }
}

object GetAllCredentials : CommandHandler("Get all credentials"), ValueHandler {
    override fun execute(data: ValueNode, context: ScriptContext): JsonNode {
        val targetName = data.asText()
        val credentials = Credentials.getFrom(context)
        val target = credentials.targetResources[targetName] ?: throw InstacliCommandError(
            "Unknown target $targetName",
            "unknown target",
            Json.newObject("target", targetName)
        )

        return target.credentials()
    }
}

object SetDefaultCredentials : CommandHandler("Set default credentials"), ObjectHandler {
    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        val targetName = data.getTextParameter("target")
        val newDefault = data.getTextParameter("name")

        val credentials = Credentials.getFrom(context)
        val target = credentials.targetResources[targetName] ?: return null
        target.default = newDefault

        credentials.save()

        return null
    }
}

object DeleteCredentials : CommandHandler("Delete credentials"), ObjectHandler {
    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        val targetName = data.getTextParameter("target")
        val oldCredentials = data.getTextParameter("name")

        val credentials = Credentials.getFrom(context)
        val target = credentials.targetResources[targetName] ?: return null
        target.credentials.removeIf { it["name"]?.textValue() == oldCredentials }

        credentials.save()

        return null
    }
}

object ConnectTo : CommandHandler("Connect to"), ValueHandler {
    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {
        if (context !is CliFileContext) {
            error("'Connect to' is only supported when running files.")
        }

        val targetName = data.textValue()
        val connectScript = context.info.connections[targetName]
            ?: throw CliScriptingException("No connection script configured for $targetName in ${context.cliFile.parent.toRealPath().name}")

        when (connectScript) {
            is ValueNode -> {
                val cliFile = context.scriptDir.resolve(connectScript.textValue())
                return CliFile(cliFile).run(CliFileContext(cliFile, context))
            }

            else -> {
                return connectScript.runScript(context)
            }
        }
    }
}

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
