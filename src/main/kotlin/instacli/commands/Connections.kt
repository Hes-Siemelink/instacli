package instacli.commands

import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.*
import instacli.cli.CliFile
import instacli.cli.CliFileContext
import instacli.cli.InstacliPaths
import instacli.script.*
import instacli.util.Json
import instacli.util.Yaml
import instacli.util.toDomainObject
import java.nio.file.Path
import kotlin.io.path.createFile
import kotlin.io.path.exists
import kotlin.io.path.name

object GetAccount : CommandHandler("Get account"), ValueHandler {
    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {
        val targetName = data.asText() ?: throw CommandFormatException("Specify connection")

        val connections = Connections.getFrom(context)
        val target = connections.targets[targetName] ?: return TextNode("")
        return when {
            target.default != null -> {
                target.defaultAccount()
            }

            target.accounts.isNotEmpty() -> {
                target.accounts.first()
            }

            else -> throw InstacliCommandError(
                "No accounts defined for $targetName",
                "no accounts",
                Json.newObject("target", targetName)
            )
        }
    }
}


object CreateAccount : CommandHandler("Create account"), ObjectHandler {
    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode {
        val newAccount = data.toDomainObject(CreateAccountInfo::class)
        val connections = Connections.getFrom(context)
        val target = connections.targets.getOrPut(newAccount.target) {
            ConnectionTarget()
        }

        target.accounts.add(newAccount.account)

        connections.save()

        return newAccount.account
    }
}

object GetAccounts : CommandHandler("Get accounts"), ValueHandler {
    override fun execute(data: ValueNode, context: ScriptContext): JsonNode {
        val targetName = data.asText()
        val connections = Connections.getFrom(context)
        val target = connections.targets[targetName] ?: throw InstacliCommandError(
            "Unknown target $targetName",
            "unknown target",
            Json.newObject("target", targetName)
        )

        return target.accounts()
    }
}

object SetDefaultAccount : CommandHandler("Set default account"), ObjectHandler {
    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        val targetName = data.getTextParameter("target")
        val account = data.getTextParameter("name")

        val connections = Connections.getFrom(context)
        val target = connections.targets[targetName] ?: return null
        target.default = account

        connections.save()

        return null
    }
}

object DeleteAccount : CommandHandler("Delete account"), ObjectHandler {
    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        val targetName = data.getTextParameter("target")
        val account = data.getTextParameter("name")

        val connections = Connections.getFrom(context)
        val target = connections.targets[targetName] ?: return null
        target.accounts.removeIf { it["name"]?.textValue() == account }

        connections.save()

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

class Connections {

    @JsonAnySetter
    var targets: MutableMap<String, ConnectionTarget> = mutableMapOf()

    @JsonIgnore
    var file: Path? = null

    fun save() {
        checkNotNull(file) { "Can't save Connections object because there is no file associated with it." }

        Yaml.mapper.writeValue(file?.toFile(), this.targets)
    }

    fun storeIn(context: ScriptContext) {
        store(context, this)
    }

    companion object {

        const val FILE_NAME = "connections.yaml"
        private val CONNECTIONS_YAML: Path = InstacliPaths.INSTACLI_HOME.resolve(FILE_NAME)

        private fun store(context: ScriptContext, value: Connections) {
            context.session[FILE_NAME] = value
        }

        fun getFrom(context: ScriptContext): Connections {
            return context.session.getOrPut(FILE_NAME) {
                load()
            } as Connections
        }

        fun load(file: Path = CONNECTIONS_YAML): Connections {

            createIfNotExists(file)

            val node = Yaml.readFile(file)
            val connections = node.toDomainObject(Connections::class)
            connections.file = file

            return connections
        }

        private fun createIfNotExists(file: Path = CONNECTIONS_YAML) {
            if (file.exists()) {
                return
            }
            file.createFile()
            val connections = Connections()
            connections.file = file
            connections.save()
        }
    }
}

class ConnectionTarget {
    var accounts: MutableList<ObjectNode> = mutableListOf()
    var default: String? = null

    fun defaultAccount(): ObjectNode? {
        return accounts.find { it["name"]?.textValue() == default }
    }

    fun accounts(): ArrayNode {
        val list = ArrayNode(JsonNodeFactory.instance)
        for (item in accounts) {
            list.add(item)
        }
        return list
    }
}

class CreateAccountInfo {
    var target: String = "Default"
    var account: ObjectNode = Json.newObject()
}
