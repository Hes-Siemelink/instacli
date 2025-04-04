# Command: Temp file

`Temp file` creates a temporary file.

| Content type | Supported |
|--------------|-----------|
| Value        | yes       |
| List         | no        |
| Object       | yes       |

[Temp file.schema.yaml](schema/Temp%20file.schema.yaml)

## Basic usage

Use **Temp file** to create a temporary file.

The command returns the file name of the created file.

```yaml instacli
Code example: Temporary file

Temp file: |
  My content
As: ${temp}

Read file: ${temp}

Expected output: |
  My content
```

The file will be deleted when the script ends.
