# Command: Validate

Validates data with JSON schema

| Content type | Supported |
|--------------|-----------|
| Value        | no        |
| List         | implicit  |
| Object       | yes       |

[Validate.schema.yaml](schema/ValidateSchema.schema.yaml)

## Basic usage

Use **Validate schema** to check if data is valid according to a JSON Schema

```yaml instacli
Code example: Validate with inline JSON schema

Validate schema:
  data: Hello world
  schema:
    type: string
    pattern: "Hello"
```

By default, Instacli uses

    https://json-schema.org/draft/2020-12/schema

## Schema from file

You can also load the schema from a file.

Suppose you have the data in a file called `myschema.json`

```yaml file=myschema.json
{
  "$schema": "https://json-schema.org/draft/2020-12/schema",
  "type": "string",
  "pattern": "Hello"
}
```

```yaml instacli
Code example: Validate with JSON schema from file

Validate schema:
  data: Hello world
  schema: myschema.json
```

Note: the file is resolved relative to the script itself, not to the working directory.

## Invalid data

If the data in `data` is invalid, you will get an error

```yaml instacli
Code example: Invalid data with JSON schema

Validate schema:
  data: Hello world
  schema:
    type: object

On error:
  Print: ${error}
  Output: validation error

Expected output: validation error
```

