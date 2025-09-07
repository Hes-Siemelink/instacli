package instacli.cli

import specscript.cli.SpecScriptMain

/**
 * Minimal CLI bootstrap that delegates to SpecScript library.
 */
fun main(args: Array<String>) {
    System.exit(SpecScriptMain.main(args))
}
