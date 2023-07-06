package instacli.cli

class CliCommandLineOptions(args: Array<String> = arrayOf<String>()) {
    val args = mutableListOf<String>()
    var interactive = false
    var help = false

    init {
        this.args.addAll(args.filter { !it.startsWith('-') })
        interactive = args.contains("-i")
        help = args.contains("--help")
    }
}