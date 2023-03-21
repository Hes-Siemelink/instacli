package hes.yak

import com.fasterxml.jackson.databind.JsonNode

interface Command {
    fun execute(node: JsonNode)
}