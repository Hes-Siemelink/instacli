package instacli.commands.userinteraction

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.BooleanNode.FALSE
import com.fasterxml.jackson.databind.node.BooleanNode.TRUE
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import com.github.kinquirer.core.Choice
import instacli.language.CommandFormatException
import instacli.language.types.ObjectProperties
import instacli.language.types.ParameterData
import instacli.language.types.PropertyDefinition
import instacli.language.types.TypeSpecification
import instacli.util.Json
import instacli.util.toDisplayYaml


fun PropertyDefinition.prompt(label: String? = null): JsonNode {
    val message = description ?: label ?: ""

    return when {
        enum != null && select == "single" ->
            promptChoice(message)

        enum != null && select == "multiple" ->
            promptChoice(message, multiple = true)

        secret ->
            promptText(message, password = true)

        type != null ->
            promptByType(message, type!!)

        else ->
            promptText(message)
    }
}

private fun PropertyDefinition.promptText(message: String, password: Boolean = false): JsonNode {
    return UserPrompt.prompt(message, default?.asText() ?: "", password)
}

private fun PropertyDefinition.promptBoolean(message: String): JsonNode {

    val answer = UserPrompt.prompt(message, default?.asText() ?: "")

    return if (answer.textValue() == "true") TRUE
    else FALSE
}

private fun PropertyDefinition.promptChoice(message: String, multiple: Boolean = false): JsonNode {

    val choices = enum?.map { choiceData ->
        if (displayProperty == null) {
            Choice(choiceData.toDisplayYaml(), choiceData)
        } else {
            Choice(choiceData[displayProperty].textValue(), choiceData)
        }
    } ?: emptyList()

    val answer = UserPrompt.select(message, choices, multiple)

    return answer.onlyWith(valueProperty)
}

private fun PropertyDefinition.promptByType(message: String, type: TypeSpecification): JsonNode {

    // Primitive types
    when (type.name) {
        "boolean" -> return promptBoolean(message)
        "string" -> return promptText(message)
        // TODO support other primitive types
    }


    // Primitive types

    return when {
        type.properties != null -> type.properties.promptObject()
        type.listOf != null -> promptList(message, type.listOf)
        type.base != null -> {
            when (type.base) {
                "boolean" -> promptBoolean(message)
                "string" -> promptText(message)
                else -> throw CommandFormatException("Base type not supported: ${type.base}")
            }
        }

        else -> throw CommandFormatException("Type not supported: $type")
    }
}

private fun ObjectProperties.promptObject(): JsonNode {

    val answers = Json.newObject()

    // Temporary variables that will hold the contents of the entries so later ones can refer to previous ones
    val variables = mutableMapOf<String, JsonNode>()

    for ((name, parameter) in properties) {

        // Only ask if condition is true
        parameter.conditionValid() || continue

        // Ask user
        val answer = parameter.prompt(name)

        // Add answer to result and to list of variables
        answers.set<JsonNode>(name, answer)
        variables[name] = answer
    }

    return answers
}

private fun JsonNode.onlyWith(field: String?): JsonNode {

    if (field == null) {
        return this
    }

    return when (this) {

        is ObjectNode -> this[field]

        is ArrayNode -> {
            val copy = arrayNode()
            for (item in this) {
                copy.add(item[field])
            }
            copy
        }

        else -> this
    }
}


private fun promptList(message: String, type: TypeSpecification): ArrayNode {
    val list = Json.newArray()

    val name = type.name ?: "item"
    val add = TextNode("Add new $name")
    val done = TextNode("Done")

    while (true) {

        val choice = ParameterData(
            enum = listOf(add, done)
        ).promptChoice(message)

        if (choice == done) {
            break
        }

        val item = ParameterData(
            type = type
        ).promptByType("Enter new item", type)
        list.add(item)
    }

    return list
}
