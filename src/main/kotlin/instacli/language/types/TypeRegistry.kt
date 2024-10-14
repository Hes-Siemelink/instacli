package instacli.language.types

import instacli.language.CliScriptingException
import instacli.language.CommandFormatException


class TypeRegistry {
    private val types = mutableMapOf<String, Type>()

    constructor() {
        registerType(Type.stringBaseType)
        registerType(Type.booleanBaseType)
        registerType(Type.objectBaseType)
        registerType(Type.arrayBaseType)
    }

    fun registerType(type: Type) {
        types[type.name] = type
    }

    fun getType(name: String): Type? {
        return types[name]
    }
}

fun TypeSpecification.resolveWith(registry: TypeRegistry): Type {

    return when {

        name != null -> {
            val type = (registry.getType(name) ?: throw CliScriptingException("Type not found: $name"))
            Type(name, type.definition.resolveTypes(registry))
        }

        else -> {
            Type("anonymous", this.resolveTypes(registry))
        }
    }
}

fun TypeSpecification.resolveTypes(registry: TypeRegistry): TypeSpecification {

    return when {

        base == Type.OBJECT || properties != null -> {
            resolveObject(registry)
        }

        base == Type.ARRAY || listOf != null -> {
            resolveArray(registry)
        }

        base != null -> {
            // Primitive type, no resolution needed
            this
        }

        else -> {
            throw IllegalStateException("Invalid type definition")
        }
    }
}

fun TypeSpecification.resolveObject(registry: TypeRegistry): TypeSpecification {

    val actualBase = base ?: Type.OBJECT
    if (actualBase != Type.OBJECT) {
        throw CommandFormatException("With properties defined on a type, base must be 'object', but was: '$base'")
    }

    if (listOf != null) {
        throw CommandFormatException("With properties defined on a type, 'list of' must not be defined")
    }

    val resolvedProperties = properties?.resolveTypes(registry) ?: ObjectProperties()

    return TypeSpecification(base = actualBase, properties = resolvedProperties)
}

fun TypeSpecification.resolveArray(registry: TypeRegistry): TypeSpecification {
    val actualBase = base ?: Type.ARRAY
    if (actualBase != Type.ARRAY) {
        throw CommandFormatException("With list defined on a type, base must be 'array', but was: '$base'")
    }

    if (properties != null) {
        throw CommandFormatException("With list defined on a type, 'properties' must not be defined")
    }

    val resolvedListOf = listOf?.resolveWith(registry)?.definition

    return TypeSpecification(base = actualBase, listOf = resolvedListOf)
}

fun ObjectProperties.resolveTypes(registry: TypeRegistry): ObjectProperties {
    return ObjectProperties(properties.mapValues { it.value.resolveTypes(registry) })
}

fun PropertySpecification.resolveTypes(registry: TypeRegistry): PropertySpecification {

    val actualType = type?.resolveWith(registry)?.definition

    return withType(actualType)
}
