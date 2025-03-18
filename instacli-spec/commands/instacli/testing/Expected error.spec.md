# Command: Expected error

`Expected error` tests if an error was raised previously.

| Content type | Supported |
|--------------|-----------|
| Value        | yes       |
| List         | no        |
| Object       | no        |

[Expected error.schema.yaml](schema/Expected%20error.schema.yaml)

## Basic usage

**Expected error** is a shortcut for checking if an error was raised. If there is an error, it will just pass. If there
is no error condition going on, then a new error is raised.

```yaml instacli
Code example: Check for an expected error

Error: PANIC

Expected error: We should have an error
```

The behavior is useful in tests, where [On error](../errors/On%20error.spec.md) swallows the exception but doesn't raise
a flag when there was _no_ error.

## Checking for a specific error

You can also check for a specific error type

```yaml instacli
Code example: Check for a specific error

Error:
  type: 13
  message: Something happened

Expected error:
  13: We should have an error
```

Like [On error type](../errors/On%20error%20type.spec.md), the `any` error type is a catch-all.

```yaml instacli
Code example: Check for an expected error

Error: PANIC

Expected error:
  any: We should have an error
```
