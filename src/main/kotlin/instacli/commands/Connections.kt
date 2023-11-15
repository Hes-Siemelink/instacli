package instacli.commands

import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.*
import instacli.cli.CliFile
import instacli.cli.CliFileContext
import instacli.cli.INSTACLI_HOME
import instacli.engine.*
import instacli.util.Yaml
import instacli.util.objectNode
import java.io.File

class GetAccount : CommandHandler("Get account"), ValueHandler {
    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {
        val targetName = data.asText() ?: throw CommandFormatException("Specify connection", data)

        val target = context.connections.targets[targetName] ?: return TextNode("")
        return when {
            target.default != null -> {
                target.defaultAccount()
            }

            target.accounts.isNotEmpty() -> {
                target.accounts.first()
            }

            else -> throw CliScriptException("No accounts defined for $targetName")
        }
    }
}


class CreateAccount : CommandHandler("Create account"), ObjectHandler {
    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode {
        val newAccount = CreateAccountInfo.from(data)
        val target = context.connections.targets.getOrPut(newAccount.target) {
            ConnectionTarget()
        }

        target.accounts.add(newAccount.account)

        context.connections.save()

        return newAccount.account
    }
}

class GetAccounts : CommandHandler("Get accounts"), ValueHandler {
    override fun execute(data: ValueNode, context: ScriptContext): JsonNode {
        val targetName = data.asText()
        val target = context.connections.targets[targetName] ?: throw CliScriptException("Unknown target $targetName")

        return target.accounts()
    }
}

class SetDefaultAccount : CommandHandler("Set default account"), ObjectHandler {
    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        val targetName = getTextParameter(data, "target")
        val account = getTextParameter(data, "name")

        val target = context.connections.targets[targetName] ?: return null
        target.default = account

        context.connections.save()

        return null
    }
}

class DeleteAccount : CommandHandler("Delete account"), ObjectHandler {
    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        val targetName = getTextParameter(data, "target")
        val account = getTextParameter(data, "name")

        val target = context.connections.targets[targetName] ?: return null
        target.accounts.removeIf { it["name"]?.textValue() == account }

        context.connections.save()

        return null
    }
}

class ConnectTo : CommandHandler("Connect to"), ValueHandler {
    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {
        val targetName = data.textValue()
        if (context is CliFileContext) {
            val connectScript = context.info.connections[targetName]
                ?: throw IllegalArgumentException("No connection script configured for $targetName in ${context.cliFile.parentFile.canonicalFile.name}")

            return CliFile(File(context.scriptDir, connectScript)).run(context)
        } else {
            error("'Connect to' is only supported when running files.")
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
    var file: File? = null

    fun save() {
        checkNotNull(file) { "Can't save Connections object because there is no file associated with it." }

        Yaml.mapper.writeValue(file, this.targets)
    }

    companion object {
        fun from(data: JsonNode): Connections {
            return Yaml.mapper.treeToValue(data, Connections::class.java)
        }

        fun load(file: File = File(INSTACLI_HOME, "connections.yaml")): Connections {
            val node = Yaml.readFile(file) ?: throw IllegalArgumentException("Connections file not found : $file")
            val instance = from(node)
            instance.file = file
            return instance
        }

        fun load(resource: String): Connections {
            val node = Yaml.readResource(resource)
            return from(node)
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
    var account: ObjectNode = objectNode()

    companion object {
        fun from(data: JsonNode): CreateAccountInfo {
            return Yaml.mapper.treeToValue(data, CreateAccountInfo::class.java)
        }
    }
}
