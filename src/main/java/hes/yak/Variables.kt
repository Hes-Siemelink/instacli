package hes.yak

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode

val VARIABLE_REGEX = Regex("\\$\\{([^}]+)}")

fun resolveVariables(data: JsonNode, variables: Map<String, JsonNode>): JsonNode {
    if (data is TextNode) {
        // Single variable reference will return full content of variable as node
        val singleVariableMatch = VARIABLE_REGEX.matchEntire(data.textValue())
        if (singleVariableMatch != null) {
            val varName = singleVariableMatch.groupValues[1]
            return variables[varName] ?: throw ScriptException("Unknown variable: \${${varName}}")
        }

        // One or more variables mixed in text are replaced with text values
        // Only replace the node is there is a variable in it
        if (VARIABLE_REGEX.containsMatchIn(data.textValue())) {
            return TextNode(resolveVariablesInText(data.textValue(), variables))
        }
    }

    // Replace elements of a list containing a variable
    if (data is ArrayNode) {
        for (i in 0 until data.size()) {
            data.set(i, resolveVariables(data.get(i), variables))
        }
    }

    // Replace elements of object containing a variable
    if (data is ObjectNode) {
        for (field in data.fields()) {
            data.set<JsonNode>(field.key, resolveVariables(field.value, variables))
        }
    }

    return data
}

fun resolveVariablesInText(raw: String, variables: Map<String, JsonNode>): String {
    val replaced = VARIABLE_REGEX.replace(raw) {
        resolve(it.groupValues[1], variables)
    }
    return replaced
}

fun resolve(varName: String, variables: Map<String, JsonNode>): String {
    // TODO pretty print as Yaml when replacing variables in text that ar eobjects or arrays
    return variables[varName]?.asText() ?: throw ScriptException("Unknown variable: \${${varName}}")
}
