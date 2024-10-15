package instacli.language.types

class Type(val name: String, var definition: TypeSpecification = TypeSpecification(base = name)) {

    companion object {
        const val STRING = "string"
        const val BOOLEAN = "boolean"
        const val OBJECT = "object"
        const val ARRAY = "array"

        val stringBaseType = Type(STRING)
        val booleanBaseType = Type(BOOLEAN)
        val objectBaseType = Type(OBJECT)
        val arrayBaseType = Type(ARRAY)
    }
}
