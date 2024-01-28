# Command: Shell

Use **Shell** to execute a shell command

| Content type | Supported                    |
|--------------|------------------------------|
| Value        | yes                          |
| List         | implicit                     |
| Object       | yes                          |
| `script`     | The name of the shell script |

## Basic usage

Call a shell command with **Shell**

```yaml
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

```yaml
Code example: Call a local shell script

Shell:
  relative: sh hello.sh

Expected output: Hello World
```

