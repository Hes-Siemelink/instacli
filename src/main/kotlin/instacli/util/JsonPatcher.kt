package instacli.util

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode

/* Derived from https://github.com/egerardus/simple-json-patch */

/**
 * Applies all JSON patch operations to a JSON document.
 * @return a copy of the JSON document with patches applied
 */
fun JsonNode.applyPatch(patch: ArrayNode): JsonNode {
    if (!isContainerNode) {
        throw IllegalArgumentException("Invalid JSON document, an object or array is required")
    }

    var result = deepCopy<JsonNode>()

    for (operation: JsonNode in patch) {
        if (!operation.isObject) {
            throw IllegalArgumentException("Invalid operation: $operation")
        }
        result = perform(operation as ObjectNode, result)
    }

    return result
}

/**
 * Perform one JSON patch operation
 */
private fun perform(operation: ObjectNode, doc: JsonNode): JsonNode {
    val opNode = operation["op"]
    if (opNode == null || !opNode.isTextual) {
        throw IllegalArgumentException("Invalid \"op\" property: $opNode")
    }
    val op = opNode.asText()
    val pathNode = operation["path"]
    if (pathNode == null || !pathNode.isTextual) {
        throw IllegalArgumentException("Invalid \"path\" property: $pathNode")
    }
    val path = pathNode.asText()
    if (path.isNotEmpty() && path[0] != '/') {
        throw IllegalArgumentException("Invalid \"path\" property: $path")
    }

    when (op) {
        "add" -> {
            val value = operation.get("value") ?: throw IllegalArgumentException("Missing \"value\" property")
            return add(doc, path, value)
        }

        "remove" -> {
            return remove(doc, path)
        }

        "replace" -> {
            val value = operation.get("value") ?: throw IllegalArgumentException("Missing \"value\" property")
            return replace(doc, path, value)
        }

        "move" -> {
            val fromNode = operation["from"]
            if (fromNode == null || !fromNode.isTextual) {
                throw IllegalArgumentException("Invalid \"from\" property: $fromNode")
            }
            val from = fromNode.asText()
            if (from.isNotEmpty() && from[0] != '/') {
                throw IllegalArgumentException("Invalid \"from\" property: $fromNode")
            }
            return move(doc, path, from)
        }

        "copy" -> {
            val fromNode = operation["from"]
            if (fromNode == null || !fromNode.isTextual) {
                throw IllegalArgumentException("Invalid \"from\" property: $fromNode")
            }
            val from = fromNode.asText()
            if (from.isNotEmpty() && from[0] != '/') {
                throw IllegalArgumentException("Invalid \"from\" property: $fromNode")
            }
            return copy(doc, path, from)
        }

        "test" -> {
            val value = operation.get("value") ?: throw IllegalArgumentException("Missing \"value\" property")
            return test(doc, path, value)
        }

        else -> throw IllegalArgumentException("Invalid \"op\" property: $op")
    }
}

/**
 * Perform a JSON patch "add" operation on a JSON document
 */
fun add(doc: JsonNode, path: String, value: JsonNode): JsonNode {
    if (path.isEmpty()) {
        return value
    }


    // get the path parent
    val lastPathIndex = path.lastIndexOf('/')
    val parent: JsonNode = if (lastPathIndex < 1) {
        doc
    } else {
        doc.at(path.substring(0, lastPathIndex))
    }


    // adding to an object
    if (parent.isObject) {
        val parentObject = parent as ObjectNode
        val key = path.substring(lastPathIndex + 1)
        parentObject.set<JsonNode>(key, value)
    } else if (parent.isArray) {
        val key = path.substring(lastPathIndex + 1)
        val parentArray = parent as ArrayNode
        if ((key == "-")) {
            parentArray.add(value)
        } else {
            try {
                val idx = key.toInt()
                if (idx > parentArray.size() || idx < 0) {
                    throw IllegalArgumentException("Array index is out of bounds: $idx")
                }
                parentArray.insert(idx, value)
            } catch (e: NumberFormatException) {
                throw IllegalArgumentException("Invalid array index: $key")
            }
        }
    } else {
        throw IllegalArgumentException("Invalid \"path\" property: $path")
    }

    return doc
}

/**
 * Perform a JSON patch "remove" operation on a JSON document
 */
fun remove(doc: JsonNode, path: String): JsonNode {
    if (path == "") {
        if (doc.isObject) {
            val docObject = doc as ObjectNode
            docObject.removeAll()
            return doc
        } else if (doc.isArray) {
            val docArray = doc as ArrayNode
            docArray.removeAll()
            return doc
        }
    }


    // get the path parent
    val lastPathIndex = path.lastIndexOf('/')
    val parent: JsonNode = if (lastPathIndex == 0) {
        doc
    } else {
        val parentPath = path.substring(0, lastPathIndex)
        doc.at(parentPath)
    }

    if (parent.isMissingNode) {
        throw IllegalArgumentException("Path does not exist: $path")
    }


    // removing from an object
    val key = path.substring(lastPathIndex + 1)
    if (parent.isObject) {
        val parentObject = parent as ObjectNode
        if (!parent.has(key)) {
            throw IllegalArgumentException("Property does not exist: $key")
        }
        parentObject.remove(key)
    } else if (parent.isArray) {
        try {
            val parentArray = parent as ArrayNode
            val idx = key.toInt()
            if (!parent.has(idx)) {
                throw IllegalArgumentException("Index does not exist: $key")
            }
            parentArray.remove(idx)
        } catch (e: NumberFormatException) {
            throw IllegalArgumentException("Invalid array index: $key")
        }
    } else {
        throw IllegalArgumentException("Invalid \"path\" property: $path")
    }

    return doc
}

/**
 * Perform a JSON patch "replace" operation on a JSON document
 */
fun replace(doc: JsonNode, path: String, value: JsonNode): JsonNode {
    val strippedDoc = remove(doc, path)
    return add(strippedDoc, path, value)
}

/**
 * Perform a JSON patch "move" operation on a JSON document
 */
fun move(doc: JsonNode, path: String, from: String): JsonNode {
    // get the value
    val value = doc.at(from)
    if (value.isMissingNode) {
        throw IllegalArgumentException("Invalid \"from\" property: $from")
    }


    // do remove and then add
    val strippedDoc = remove(doc, from)
    return add(strippedDoc, path, value)
}

/**
 * Perform a JSON patch "copy" operation on a JSON document
 */
fun copy(doc: JsonNode, path: String, from: String): JsonNode {
    // get the value
    val value = doc.at(from)
    if (value.isMissingNode) {
        throw IllegalArgumentException("Invalid \"from\" property: $from")
    }


    // do add
    return add(doc, path, value)
}

/**
 * Perform a JSON patch "test" operation on a JSON document
 */
fun test(doc: JsonNode, path: String, value: JsonNode): JsonNode {
    val node = doc.at(path)
    if (node.isMissingNode) {
        throw IllegalArgumentException("Invalid \"path\" property: $path")
    }

    if (node != value) {
        throw IllegalArgumentException("The value does not equal path node")
    }

    return doc
}
