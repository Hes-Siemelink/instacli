# Command: Find

`Find` retrieves a snippet of JSON from a larger object

| Content type | Supported |
|--------------|-----------|
| Value        | no        |
| List         | implicit  |
| Object       | yes       |

[Find.schema.yaml](schema/Find.schema.yaml)

## Basic usage

Add some numbers

```yaml instacli
Code example: Find a snippet based on a variable path

${language}: English

Find:
  path: ${language}
  in:
    English: Hello
    Spanish: Hola
    Dutch: Hallo

Expected output: Hello
```

