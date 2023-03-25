package hes.yak

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode

val VARIABLE_REGEX = Regex("\\$\\{([^}]+)}")

fun resolveVariables(data: JsonNode, variables: Map<String, JsonNode>): JsonNode {
    // Replace text if it contains a variable
    if (data is TextNode && VARIABLE_REGEX.containsMatchIn(data.textValue())) {
        return TextNode(resolveVariablesInText(data.textValue(), variables))
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

fun resolve(match: String, variables: Map<String, JsonNode>): String {
    return variables[match]?.asText() ?: throw ScriptException("Unknown variable: \${${match}}")
}
