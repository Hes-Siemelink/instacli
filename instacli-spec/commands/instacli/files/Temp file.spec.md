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

## Temp file with name

You can also specify a name for the temporary file. The file will be created in the `SCRIPT_TEMP_DIR` directory.

```yaml instacli
Code example: Temporary file with specified name

Temp file:
  filename: myfile.txt
  content: |
    My content

Read file: ${SCRIPT_TEMP_DIR}/myfile.txt

Expected output: |
  My content
```

## Resolve variables

You can use variables in the content of the temporary file. The variables will be resolved when the file is created.

```yaml instacli
Code example: Temporary file with variables

${name}: Carol

Temp file: |
  Hello ${name}
As: ${temp}

Read file: ${temp}

Expected output: |
  Hello Carol
```

In some cases, you don't want to resolve the variables in the content of the temporary file. You can use the `resolve`
property to control this.

```yaml instacli
Code example: Temporary file with variables not resolved

Temp file:
  filename: goodbye.cli
  resolve: false
  content:
    Output: Adios ${input.name}
As: ${temp}

Goodbye:
  name: Daniel

Expected output: Adios Daniel
```