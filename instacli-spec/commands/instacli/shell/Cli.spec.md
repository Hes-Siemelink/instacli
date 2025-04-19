# Command: Cli

With **Cli** you execute the Instacli command without popping into a new shell. This is mainly useful to document and
test the behavior of the `cli` command line interface.

| Content type | Supported |
|--------------|-----------|
| Value        | yes       |
| List         | implicit  |
| Object       | no        |

[Cli.schema.yaml](schema/Cli.schema.yaml)

## Basic usage

Show Instacli help

```yaml instacli
Code example: Execute Instacli command

Cli: --help

Expected console output: |
  Instacli -- Instantly create CLI applications with light scripting!

  Usage:
     cli [global options] file | directory [command options]

  Global options:
    --help, -h          Print help on a script or directory and does not run anything
    --output, -o        Print the output at the end of the script in Yaml format
    --output-json, -j   Print the output at the end of the script in Json format
    --non-interactive, -q   Indicate that Instacli should not prompt for user input
    --debug, -d         Run in debug mode. Prints stacktraces when an error occurs.
```

## Specifying the working dir

Use the long format to specify the working directory. This is useful when you want to run a command in a different
directory than the one where the script is located.

```yaml instacli
Code example: Cli in a different directory

Cli:
  command: cli -q basic
  cd: samples

Expected console output: |
  Simple Instacli example scripts

  Available commands:
    create-greeting   Creates a greeting and puts it in the output
    greet             Prints a greeting
    multiple-choice   Interaction example
    output            Sets test output
    prompt            Simple interactive prompt
```