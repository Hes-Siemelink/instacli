# Command: Expected error

`Expected error` tests if an error was raised previously.

| Content type | Supported |
|--------------|-----------|
| Value        | yes       |
| List         | no        |
| Object       | no        |

## Basic usage

**Expected error** is a shortcut for checking if an error was raised. If there is an error, it will just pass. If there
is no error condition going on, then a new error is raised.

```yaml instacli
Code example: Check for an expected error

Error: PANIC

Expected error: We should have an error
```

The behavior is useful in tests, where [On error](../errors/On%20error.md) swallows the exception but doesn't raise a
flag when there was an error that you expected to happen.

