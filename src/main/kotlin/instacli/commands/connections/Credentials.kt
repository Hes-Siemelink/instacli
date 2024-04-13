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
import instacli.util.updateWith
import java.nio.file.Path
import kotlin.io.path.createFile
import kotlin.io.path.exists

object Credentials {

    const val FILE_NAME = "credentials.yaml"
    private val DEFAULT_FILE: Path = InstacliPaths.INSTACLI_HOME.resolve(FILE_NAME)

    fun fromFile(file: Path = DEFAULT_FILE): CredentialsFile {

        return if (file.exists()) {
            val content = Yaml.readFile(file)
            CredentialsFile(file).updateWith(content)
        } else {
            file.createFile()
            CredentialsFile(file).save()
        }
    }
}

fun CredentialsFile.save(): CredentialsFile {
    checkNotNull(file) { "Can't save Credentials object because there is no file associated with it." }

    Yaml.mapper.writeValue(file?.toFile(), this.targetResources)

    return this
}

fun ScriptContext.setCredentials(credentials: CredentialsFile) {
    session[Credentials.FILE_NAME] = credentials
}

fun ScriptContext.getCredentials(): CredentialsFile {
    return session.getOrPut(Credentials.FILE_NAME) {
        Credentials.fromFile()
    } as CredentialsFile
}


//
// Data model
//

class CredentialsFile(@JsonIgnore var file: Path? = null) {

    @JsonAnySetter
    var targetResources: MutableMap<String, TargetResource> = mutableMapOf()
}

class TargetResource {

    var credentials: MutableList<ObjectNode> = mutableListOf()
    var default: String? = null

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

class CreateCredentialsInfo {

    var target: String = "Default"
    var credentials: ObjectNode = Json.newObject()
}
