# Instacli command-line options

You run an Instacli file or directory with the `cli` command.

## Global options

When running `cli` or `cli --help`, the global options will be printed

```commandline cli
cli
```

```cli output
Instacli -- Instantly create CLI applications with light scripting!

Usage:
   cli [global options] file | directory [command options]

Global options:
  --help, -h          Print help on a script or directory and does not run anything
  --print-output, -o   Print the output at the end of the script
  --non-interactive, -q   Indicate that Instacli should not prompt for user input
  --debug, -d         Run in debug mode. Prints stacktraces when an error occurs.
```

### --help

The `--help` option prints help on a script or directory and then exits. No scripts are run.

For this example we run from the **[samples](/samples)** directory. It contains a directory `basic`. Let's use
the `--help` option to see what Instacli commands it contains

```commandline cli directory:samples
cli --help basic
```

```cli output
Simple Instacli example scripts

Available commands:
  create-greeting   Creates a greeting and puts it in the output
  greet             Prints a greeting
  multiple-choice   Interaction example
  output            Sets test output
  simple-question   Simple interactive prompt
```

Using `--help` on the **[greet](/samples/basic/greet.cli)** command will give us a description and show which command
line options it supports

```commandline cli directory:samples
cli --help basic greet
```

```cli output
Prints a greeting

Options:
  --name   Your name
```

With that information we can call it with a parameter that is specific to that command:

```commandline cli directory:samples
cli basic greet --name Alice
```

With the expected output:

```cli output
Hello, Alice!
```

### --print-output

Some Instacli commands will produce output. By default, Instacli does not print the output. You can turn it on with
the `--print-output` option.

For example, the **[greet](/samples/basic/greet.cli)** script uses a **Print** command to show the greeting, whereas
**[create-greeting](/samples/basic/create-greeting.cli)** does not print anything but creates output to be used by
another script.

Running `create-greeting` like this will show nothing

```commandline cli directory:samples
cli basic create-greeting --name Bob
```

Output:

```cli output
```

We will se the output when passing the `--print-output` parameter, or its shortcut `-o`:

```commandline cli directory:samples
cli -o basic create-greeting --name Bob
```

```cli output
Hello Bob!
```

### --non-interactive

Instacli may ask the user for input. For example, when prompting a missing input parameter or selecting a command from a
directory.

By default, Instacli runs in **interactive mode**. Use the `--non-interactive` or `-q` option to avoid any prompting.
The net effect is that the script will stop when user interaction is required

For example, run Instacli on a directory will pop up a command chooser:

```commandline
cli basic
```

```
Simple Instacli example scripts

* Available commands: 
 > create-greeting   Creates a greeting and puts it in the output
   greet             Prints a greeting
   multiple-choice   Interaction example
   output            Sets test output
   simple-question   Simple interactive prompt
```

After choosing a command with cursor keys and enter, ths script will continue.

If we pass `--non-interactive` however, the script will just print the list of available commands and exit:

```commandline cli directory:samples
cli --non-interactive basic
```

```cli output
Simple Instacli example scripts

Available commands:
  create-greeting   Creates a greeting and puts it in the output
  greet             Prints a greeting
  multiple-choice   Interaction example
  output            Sets test output
  simple-question   Simple interactive prompt
```

When calling a script with a missing parameter in interactive mode, you will get a question on the command line.

```commandline cli directory:samples
cli basic create-greeting
```

```cli output
? Your name
```

In non-interactive mode, the script will fail with an error message

```commandline cli directory:samples
cli -q basic create-greeting
```

```cli output
Missing parameter: --name

Options:
  --name   Your name
```

### --debug

Use this option to see stacktraces from the underlying runtime when an internal error occurs. This option is meant for
troubleshooting the Instacli runtime.

For example, the file `error-in-add.cli` has an error in it that is not handled by Instacli.

```yaml file:script-with-error.cli
GET: http:\\localhost  # Malformed URL - not caught by Instacli runtime
```

Without debug mode you get the following error message

```commandline cli
cli script-with-error.cli
```

```cli output
Instacli scripting error

Caused by: java.net.URISyntaxException: Illegal character in opaque part at index 5: http:\\localhost

In script-with-error.cli:

  GET: http:\\localhost
```

With the `--debug` option you will see more of the internals.

```commandline cli
cli --debug script-with-error.cli
```

```
Instacli scripting error

Caused by: java.net.URISyntaxException: Illegal character in opaque part at index 5: http:\\localhost
	at java.base/java.net.URI$Parser.fail(URI.java:2976)
	at java.base/java.net.URI$Parser.checkChars(URI.java:3147)
	at java.base/java.net.URI$Parser.parse(URI.java:3183)
	at java.base/java.net.URI.<init>(URI.java:623)
	at instacli.commands.HttpCommandsKt.processRequestWithoutBody(HttpCommands.kt:150)
	at instacli.commands.HttpCommandsKt.access$processRequestWithoutBody(HttpCommands.kt:1)
	at instacli.commands.HttpGet.execute(HttpCommands.kt:52)
	at instacli.script.CommandExecutionKt.handleCommand(CommandExecution.kt:82)
	at instacli.script.CommandExecutionKt.runSingleCommand(CommandExecution.kt:59)
	at instacli.script.CommandExecutionKt.runCommand(CommandExecution.kt:20)
	at instacli.script.Script.runScript(Script.kt:23)
	at instacli.cli.CliFile.run(CliFile.kt:30)
	at instacli.cli.InstacliMain.invokeFile(Main.kt:88)
	at instacli.cli.InstacliMain.run(Main.kt:61)
	at instacli.cli.InstacliMain.run$default(Main.kt:39)
	at instacli.cli.InstacliMain$Companion.main(Main.kt:161)
	at instacli.cli.InstacliMain$Companion.main$default(Main.kt:151)
	at instacli.cli.MainKt.main(Main.kt:21)

In error.cli:

  GET: http:\\localhost
```

This may help the Instacli runtime developer to diagnose the problem and fix it,