package instacli.cli

import specscript.cli.InstacliMain

/**
 * Minimal CLI bootstrap that delegates to SpecScript library.
 */
fun main(args: Array<String>) {
    System.exit(InstacliMain.main(args))
}
