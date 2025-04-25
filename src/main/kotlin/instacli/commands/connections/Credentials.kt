package instacli.commands.connections

import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.ValueNode
import instacli.cli.InstacliPaths
import instacli.language.CommandHandler
import instacli.language.ScriptContext
import instacli.language.ValueHandler
import instacli.util.Json
import instacli.util.Yaml
import instacli.util.updateWith
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.createFile
import kotlin.io.path.exists

object Credentials : CommandHandler("Credentials", "instacli/shell"), ValueHandler {

    const val FILENAME = "credentials.yaml"
    private val DEFAULT_FILE: Path = InstacliPaths.INSTACLI_HOME.resolve(FILENAME)

    fun fromFile(file: Path = DEFAULT_FILE): CredentialsFile {

        return if (file.exists()) {
            val content = Yaml.readFile(file)
            CredentialsFile(file).updateWith(content)
        } else {
            file.parent.createDirectories()
            file.createFile()
            CredentialsFile(file).save()
        }
    }

    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {
        val credentials = fromFile(Path.of(data.textValue()).toAbsolutePath())
        context.setCredentials(credentials)

        return null
    }
}

fun CredentialsFile.save(): CredentialsFile {
    checkNotNull(file) { "Can't save Credentials object because there is no file associated with it." }

    Yaml.mapper.writeValue(file?.toFile(), this.targetResources)

    return this
}

fun ScriptContext.setCredentials(credentials: CredentialsFile) {
    session[Credentials.FILENAME] = credentials
}

fun ScriptContext.getCredentials(): CredentialsFile {
    return session.getOrPut(Credentials.FILENAME) {
        Credentials.fromFile()
    } as CredentialsFile
}


//
// Data model
//

class CredentialsFile(@JsonIgnore val file: Path? = null) {
    @JsonAnySetter
    var targetResources: MutableMap<String, TargetResource> = mutableMapOf()
}

data class TargetResource(
    val credentials: MutableList<ObjectNode> = mutableListOf(),
    var default: String? = null
) {

    fun default(): ObjectNode? {
        return credentials.find { it["name"]?.textValue() == default }
    }

    fun toArrayNode(): ArrayNode {
        val list = ArrayNode(JsonNodeFactory.instance)
        for (item in credentials) {
            list.add(item)
        }
        return list
    }
}

data class CreateCredentialsInfo(
    val target: String = "Default",
    val credentials: ObjectNode = Json.newObject()
)
