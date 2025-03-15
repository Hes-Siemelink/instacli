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

## Calling a script next to your Instacli file

Shell commands are executed in the current working directory from where you launched the script. Sometimes it useful to
have a helper script next to your Instacli file. Then you would use the `local` parameter to indicate that.

Suppose you have a shell script `hello.sh`

```yaml file:hello.sh
echo "Hello World"
```

Then you can call it from an Instacli file by using the `local` parameter on **Shell**:

```yaml instacli
Code example: Call a local shell script

Shell:
  resource: sh hello.sh

Expected output: Hello World
```

