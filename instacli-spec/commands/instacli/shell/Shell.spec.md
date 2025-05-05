# Command: Shell

Use **Shell** to execute a shell command

| Content type | Supported |
|--------------|-----------|
| Value        | yes       |
| List         | implicit  |
| Object       | yes       |

[Shell.schema.yaml](schema/Shell.schema.yaml)

## Basic usage

Call a shell command with **Shell**

```yaml instacli
Code example: Execute a shell command

Shell: echo Hello

Expected output: Hello
```

## Long format

You can also pass the shell command in the `command` field. It is equivalent to the basic usage where you pass the
command as a string.

```yaml instacli
Code example: Shell command with command parameter

Shell:
  command: echo Hello

Expected output: Hello
```

This option is needed if you want to pass multiple parameters, like `show output` (see below).

## Working directory

You can set the working directory with the `cd` property.

```yaml instacli
Code example: Shell command with dir

Shell:
  cd: /tmp
  command: echo Hello
```

One example is to use the `SCRIPT_TEMP_DIR` variable to set the working directory to the temporary directory of the
script. this is where temp files are stored.

```yaml instacli
Code example: Shell command with SCRIPT_TEMP_DIR

Temp file:
  filename: temp.txt
  content: Hello

Shell:
  cd: ${SCRIPT_TEMP_DIR}
  command: cat temp.txt

Expected output: Hello
```

You can write it in short form

    ```shell cd=${SCRIPT_TEMP_DIR}
    cat temp.txt temp.txt
    ```

This will display as:

```shell cd=${SCRIPT_TEMP_DIR}
cat temp.txt temp.txt
```

And give the output:

```output
HelloHello
```

## Displaying the output

By the default, the console output is hidden. Show it with the `show output` parameter:

```yaml instacli
Code example: Show the output of a shell command on the console

Shell:
  command: echo Hello World
  show output: true

Expected console output: Hello World
```

## Displaying the shell command

It may be useful to show the command that is executed. For example, when using variables, you want to see what command
is actually executed. You can do this with the `show command` parameter.

```yaml instacli
Code example: Echo the command itself

${name}: Alice

Shell:
  command: echo Hello ${name}
  show command: true

Expected console output: echo Hello Alice
```

## Ignoring the output

By default, the output of the shell script is stored in the output variable when the shell command is executed. By
setting `capture` to false, the output is not stored in the output variable, and you retain the previous value.

This is useful if the output of a shell command will be big and irrelevant to the script.

```yaml instacli
Code example: Don't store the output of a shell command

Output: original output

Shell:
  command: echo Hello World
  capture output: false

Expected output: original output
```

## Using variables

All variables from your script are available in the shell command.

```yaml instacli
Code example: Exposed variables

${name}: Alice

Shell:
  command: echo Hello $name

Expected output: Hello Alice
```

## Passing Environment variables

You can set environment variables for the shell explicitly using the `env` parameter.

```yaml instacli
Code example: Environment variables

Shell:
  command: echo Hello $NAME
  env:
    NAME: Alice

Expected output: Hello Alice
```

## Calling a script next to your Instacli file

Shell commands are executed in the current working directory from where you launched the script. Sometimes it useful to
have a helper script next to your Instacli file. Then you would use the `resource` parameter to indicate that.

Suppose you have a shell script `hello.sh`

```yaml file=hello.sh
echo "Hello World"
```

Then you can call it from an Instacli file by using the `resource` parameter on **Shell**:

```yaml instacli
Code example: Call a local shell script

Shell:
  resource: sh hello.sh

Expected output: Hello World
```

## Using SCRIPT_DIR

You can achieve the same by using the `SCRIPT_DIR` variable.

```yaml instacli
Code example: Use SCRIPT_DIR

Shell: sh $SCRIPT_DIR/hello.sh

Expected output: Hello World
```

## Error handling

Errors in the shell command can be caught by the On error command.

```yaml instacli
Code example: Handle shell errors

Shell: exit 1

Output: Error was ignored

On error:
  Output: Error was handled

Expected output: Error was handled
```