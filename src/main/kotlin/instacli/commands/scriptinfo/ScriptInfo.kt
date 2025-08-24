package instacli.commands.scriptinfo

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.ValueNode
import instacli.commands.scriptinfo.InputParameters.handleInput
import instacli.language.*
import instacli.language.types.TypeSpecification
import instacli.language.types.resolve
import instacli.util.toDomainObject

object ScriptInfo : CommandHandler("Script info", "instacli/script-info"),
    ObjectHandler, ValueHandler, DelayedResolver {

    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {
        return null
    }

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        val scriptInfoData = data.toDomainObject(ScriptInfoData::class)

        if (scriptInfoData.input != null) {
            handleInput(context, scriptInfoData)
        }

        if (scriptInfoData.inputType != null) {
            handleInputType(context, scriptInfoData.inputType)
        }

        return context.getInputVariables()
    }
}

private fun handleInputType(
    context: ScriptContext,
    inputType: TypeSpecification
) {

    val input = inputType.resolve(context.types).definition

    if (input.properties != null) {
        handleInput(context, input.properties)
    } else {
        // TODO handle array and simple types
    }
}


