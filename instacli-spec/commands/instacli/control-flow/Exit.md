# Command: Exit

`Exit` stops running the current script.

| Content type | Supported |
|--------------|-----------|
| Value        | yes       |
| List         | yes       |
| Object       | yes       |

[Exit.schema.yaml](schema/Exit.schema.yaml)

## Basic usage

**Exit** is used to stop processing the current script. It will return the exit value to the calling script or to the
shell.

```yaml instacli
Code example: Exit from script

Exit: 0

Expected output: We are not supposed to get here
```