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

fun TypeSpecification.resolve(registry: TypeRegistry): Type {
    return Resolver(registry).resolve(this)
}

private class Resolver(val registry: TypeRegistry, val seen: MutableSet<String> = mutableSetOf()) {

    fun resolve(type: TypeSpecification): Type {
        return when {

            type.name != null -> {
                val foundType =
                    (registry.getType(type.name) ?: throw CliScriptingException("Type not found: ${type.name}"))
                seen.add(type.name)
                foundType.definition = resolveDefinition(foundType.definition)
                foundType
            }

            else -> {
                Type("anonymous", resolveDefinition(type))
            }
        }
    }

    fun resolveDefinition(type: TypeSpecification): TypeSpecification {

        return when {

            type.base == Type.OBJECT || type.properties != null -> {
                resolveObject(type)
            }

            type.base == Type.ARRAY || type.listOf != null -> {
                resolveArray(type)
            }

            type.base != null -> {
                // Primitive type, no resolution needed
                type
            }

            else -> {
                throw IllegalStateException("Invalid type definition")
            }
        }
    }

    fun resolveObject(type: TypeSpecification): TypeSpecification {

        val actualBase = type.base ?: Type.OBJECT

        // TODO Move validation somewhere else
        if (actualBase != Type.OBJECT) {
            throw CommandFormatException("With properties defined on a type, base must be 'object', but was: '${type.base}'")
        }
        if (type.listOf != null) {
            throw CommandFormatException("With properties defined on a type, 'list of' must not be defined")
        }

        val resolvedProperties = resolveProperties(type.properties)

        return TypeSpecification(base = actualBase, properties = resolvedProperties)
    }

    fun resolveArray(type: TypeSpecification): TypeSpecification {
        val actualBase = type.base ?: Type.ARRAY

        // TODO Move validation somewhere else
        if (actualBase != Type.ARRAY) {
            throw CommandFormatException("With list defined on a type, base must be 'array', but was: '${type.base}'")
        }
        if (type.properties != null) {
            throw CommandFormatException("With list defined on a type, 'properties' must not be defined")
        }

        val resolvedListOf = type.listOf?.resolve(registry)?.definition

        return TypeSpecification(base = actualBase, listOf = resolvedListOf)
    }


    fun resolveProperties(obj: ObjectProperties?): ObjectProperties {
        obj ?: return ObjectProperties()

        return ObjectProperties(obj.properties.mapValues { resolveProperty(it.value) })
    }

    fun resolveProperty(property: PropertySpecification): PropertySpecification {

        if (property.type == null) {
            return property
        }

        if (seen.contains(property.type.name)) {
            val foundType = registry.getType(property.type.name!!)!!
            return property.withType(foundType.definition)
        }

        val actualType = resolve(property.type).definition

        return property.withType(actualType)
    }
}