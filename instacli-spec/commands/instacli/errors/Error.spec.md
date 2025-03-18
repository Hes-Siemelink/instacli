# Command: Error

Error handling in CLI scripts

| Content type | Supported |
|--------------|-----------|
| Value        | yes       |
| List         | no        |
| Object       | Yes       |

[Error.schema.yaml](schema/Error.schema.yaml)

## Basic usage

Use **Error** to raise an error and **[On error](On%20error.spec.md)** to handle it.

```yaml instacli
Code example: Error handling

Error: Panic!

Output: We never got here

---
On error:
  Output: Error is handled

Expected output: Error is handled
```

## Error type and data

You can pass an error type and additional data when creating an error

```yaml instacli

Code example: Error with type and data

Error:
  message: Something happened
  type: 13
  data:
    diagnostics: example

On error type:
  13:
    Print: ${error.message}
    Output: ${error.data}

Expected output:
  diagnostics: example
```