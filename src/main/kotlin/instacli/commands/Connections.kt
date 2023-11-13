package instacli.commands

import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.JsonNode
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
                target.accounts[target.default]
            }

            target.accounts.isNotEmpty() -> {
                target.accounts.iterator().next().value
            }

            else -> throw CliScriptException("No connections defined for $targetName")
        }
    }
}


class AddConnection : CommandHandler("Add connection"), ObjectHandler {
    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        val newConnection = AddConnectionInfo.from(data)
        val target = context.connections.targets.getOrPut(newConnection.target) {
            ConnectionTarget()
        }

        target.accounts[newConnection.name] = newConnection.properties

        context.connections.save()

        return newConnection.properties
    }
}

class Connections {

    @JsonAnySetter
    var targets: MutableMap<String, ConnectionTarget> = mutableMapOf()

    @JsonIgnore
    var file: File? = null

    fun save() {
        if (file == null) throw IllegalStateException("Can't save Connections object because there is no file associated with it.")

        Yaml.mapper.writeValue(file, this.targets)
    }

    companion object {
        fun from(data: JsonNode): Connections {
            return Yaml.mapper.treeToValue(data, Connections::class.java)
        }

        fun load(file: File = File(INSTACLI_HOME, "connections.yaml")): Connections {
            val node = Yaml.readFile(file) ?: throw IllegalArgumentException("Connections file not found : $file")
            return from(node)
        }

        fun load(resource: String): Connections {
            val node = Yaml.readResource(resource)
            return from(node)
        }
    }
}

class ConnectionTarget {
    var accounts: MutableMap<String, ObjectNode> = mutableMapOf()
    var default: String? = null
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
