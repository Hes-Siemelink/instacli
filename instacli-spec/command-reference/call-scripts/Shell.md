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

## Calling another script

Suppose you have a shell script `hello.sh`

```yaml file:hello.sh
echo "Hello World"
```

Then you can call it from an Instacli file by using the `script` parameter on **Shell**:

```yaml
Code example: Call a shell script

Shell:
  script: hello.sh

Expected output: Hello World
```

