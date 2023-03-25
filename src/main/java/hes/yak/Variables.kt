package hes.yak

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.TextNode

fun resolveVariables(data: JsonNode, variables: Map<String, JsonNode>): JsonNode {
    if (data is TextNode) {
        return TextNode(resolveVariablesInText(data.textValue(), variables))
    }
    return data
}

fun resolveVariablesInText(raw: String, variables: Map<String, JsonNode>): String {
    val variableRegex = Regex("\\$\\{([^}]+)}")
    val replaced = variableRegex.replace(raw) {
        resolve(it.groupValues[1], variables)
    }
    return replaced
}

fun resolve(match: String, variables: Map<String, JsonNode>): String {
    return variables[match]?.asText() ?: throw ScriptException("Unknown variable: \${${match}}")
}
