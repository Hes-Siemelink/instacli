# Command: Test case

`Test case` marks the start of test code.

| Content type | Supported |
|--------------|-----------|
| Value        | yes       |
| List         | no        |
| Object       | no        |

[Test case.schema.yaml](schema/Test%20case.schema.yaml)

## Basic usage

With **Test case** you give a name to a test. The test runner sees the **Test case** command as the beginning of a test.

```yaml instacli
Code example: A simple test case

Test case: A simple test case

Assert that:
  item: one
  in: [ one, two, three ]
```

## Multiple test cases

If there are multiple test cases in a file, it will take all the commands from one Test case command to another as one
test.

```yaml instacli
Code example: Multiple tests

---
Test case: Test 1

Output: one

Expected output: one

---
Test case: Test 2

---
Output: two

Expected output: two

```
