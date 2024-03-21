package instacli.language

import com.fasterxml.jackson.core.JsonPointer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.TextNode
import instacli.util.JsonProcessor
import instacli.util.toDisplayYaml

val VARIABLE_REGEX = Regex("\\$\\{([^}]+)}")

fun JsonNode.resolveVariables(variables: Map<String, JsonNode>): JsonNode {
    return VariableResolver(variables).process(this)
}

private class VariableResolver(val variables: Map<String, JsonNode>) : JsonProcessor() {

    override fun processText(node: TextNode): JsonNode {

        // Single variable reference will return full content of variable as node
        val singleVariableMatch = VARIABLE_REGEX.matchEntire(node.textValue())
        if (singleVariableMatch != null) {
            val varName = singleVariableMatch.groupValues[1]
            return getValue(varName, variables)
        }

        // One or more variables mixed in text are replaced with text values
        // Only replace the node is there is a variable in it
        if (VARIABLE_REGEX.containsMatchIn(node.textValue())) {
            return TextNode(resolveVariablesInText(node.textValue(), variables))
        }

        return node
    }
}

fun resolveVariablesInText(raw: String, variables: Map<String, JsonNode>): String {
    val replaced = VARIABLE_REGEX.replace(raw) {
        getValue(it.groupValues[1], variables).toDisplayYaml()
    }
    return replaced
}

fun getValue(varName: String, variables: Map<String, JsonNode>): JsonNode {

    val variableWithPath: VariableWithPath = splitIntoVariableAndPath(varName)

    if (!variables.containsKey(variableWithPath.name)) {
        // FIXME Produces message: "Unknown variable 'greeting' in ${greeting}"
        throw CliScriptingException("Unknown variable \${${variableWithPath.name}}")
    }

    val value = variables[variableWithPath.name]!!

    return if (variableWithPath.path == null) {
        value
    } else {
        val jsonPointer = toJsonPointer(variableWithPath.path)
        value.at(jsonPointer)
    }
}

fun splitIntoVariableAndPath(varName: String): VariableWithPath {

    val split = Regex("(.*?)([\\[.].*\$)")
    val match = split.find(varName) ?: return VariableWithPath(varName, null)

    return VariableWithPath(match.groupValues[1], match.groupValues[2])
}

fun toJsonPointer(jsonPath: String): JsonPointer {

    // Make sure path starts with '.' or '['
    var result = if (jsonPath.startsWith('.') || jsonPath.startsWith('[')) jsonPath else ".$jsonPath"

    // Convert "." to "/"
    result = result.replace('.', '/')

    // Convert array indexes
    val index = Regex("\\[(\\d*)]")
    result = index.replace(result, "/$1")

    val pointer = JsonPointer.compile(result)

    return pointer
}

data class VariableWithPath(val name: String, val path: String?)

