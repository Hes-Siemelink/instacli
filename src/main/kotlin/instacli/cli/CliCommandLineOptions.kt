package instacli.cli

class CliCommandLineOptions(args: Array<String>) {
    var interactive = false
    val args = mutableListOf<String>()

    init {
        this.args.addAll(args.filter { !it.startsWith('-') })
        interactive = args.contains("-i")
    }
}