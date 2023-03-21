package hes.yak

class YakScript(val contents: String) {

    companion object {
        fun load(resource: String): YakScript {
            val raw = this::class.java.getResource(resource).readText()
            return YakScript(raw)
        }
    }
}

