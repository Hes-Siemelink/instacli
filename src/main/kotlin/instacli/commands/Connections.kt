package instacli.commands

import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.ValueNode
import instacli.cli.INSTACLI_HOME
import instacli.engine.*
import instacli.util.Yaml
import instacli.util.objectNode
import java.io.File

class Connection : CommandHandler("Connection"), ValueHandler {
    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {
        val targetName = data.asText() ?: throw CommandFormatException("Specify connection", data)

        val target = context.connections.targets[targetName] ?: throw CliScriptException("Unknown target $targetName")
        return when {
            target.default != null -> {
                target.defaultAccount()
            }

            target.accounts.isNotEmpty() -> {
                target.accounts.first()
            }

            else -> throw CliScriptException("No connections defined for $targetName")
        }
    }
}


class AddConnection : CommandHandler("Add connection"), ObjectHandler {
    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode {
        val newConnection = AddConnectionInfo.from(data)
        val target = context.connections.targets.getOrPut(newConnection.target) {
            ConnectionTarget()
        }

        target.accounts.add(newConnection.properties)

        context.connections.save()

        return newConnection.properties
    }
}

class GetAccounts : CommandHandler("Get accounts"), ValueHandler {
    override fun execute(data: ValueNode, context: ScriptContext): JsonNode {
        val targetName = data.asText()
        val target = context.connections.targets[targetName] ?: throw CliScriptException("Unknown target $targetName")

        return target.accounts()
    }
}

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

class AddConnectionInfo {
    var target: String = "Default"
    var name: String = "default"
    var properties: ObjectNode = objectNode()

    companion object {
        fun from(data: JsonNode): AddConnectionInfo {
            return Yaml.mapper.treeToValue(data, AddConnectionInfo::class.java)
        }
    }
}
