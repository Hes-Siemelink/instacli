# Command: On error type

Error handling in CLI scripts

| Content type | Supported |
|--------------|-----------|
| Value        | no        |
| List         | implicit  |
| Object       | Yes       |

[On error type.schema.yaml](schema/On%20error%20type.schema.yaml)

## Basic usage

Use **[Error](Error.spec.md)** to raise an error with a speicifc identifie and **On error** to handle it.

```yaml instacli
Code example: Error handling

Error:
  message: Internal error
  type: panic

Output: We never get here

---
On error type:
  panic:
    Output: Don't panic

Expected output: Don't panic
```

## Catch any error

You can use the 'any' keyword to as a catch-all.

```yaml instacli
Code example: Catch any exception

Error:
  message: Internal error
  type: panic

---
On error type:
  any:
    Output: Error is handled

Expected output: Error is handled
```
