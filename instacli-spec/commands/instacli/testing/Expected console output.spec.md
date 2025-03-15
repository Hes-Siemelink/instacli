# Command: Expected console output

`Expected console output` checks the console output of previous commands.

| Content type | Supported |
|--------------|-----------|
| Value        | yes       |
| List         | no        |
| Object       | no        |

[Expected console output.schema.yaml](schema/Expected%20console%20output.schema.yaml)

## Basic usage

**Expected console output** tests the printed output of the script.

```yaml instacli
Code example: Test console output

Print: Hello world!

Expected console output: Hello world!
```

## Scope: Code example and Test case

The output buffer to check against is only recorded when a new `Code example` or `Test case` is started. It is reset for
each time a new example or test case is started.

```yaml instacli
Code example: Test new console output

Print: Hello again

Expected console output: Hello again
```

For test cases:

```yaml instacli
Test case: Output 1

Print: one

Expected console output: one

---
Test case: Output 2

Print: two

Expected console output: two
```
