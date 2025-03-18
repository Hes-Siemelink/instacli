# Command: On error

Error handling in CLI scripts

| Content type | Supported |
|--------------|-----------|
| Value        | no        |
| List         | implicit  |
| Object       | Yes       |

[On error.schema.yaml](schema/On%20error.schema.yaml)

## Basic usage

Use **[Error](Error.spec.md)** to raise an error and **On error** to handle it.

```yaml instacli
Code example: Error handling

Error: Panic!

Output: We never get here

---
On error:
  Output: Error is handled

Expected output: Error is handled
```

## The error variable

You get the contents of the error as the `${error}` variable.

```yaml instacli
Code example: The error variable

Error:
  message: Something happened
  type: -1

On error:
  Output: ${error.message}

Expected output: Something happened
```
