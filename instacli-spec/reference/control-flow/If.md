# Command: If

`If` exectues commands if a condition holds

| Content type                | Supported |
|-----------------------------|-----------|
| Value                       | no        |
| List                        | implicit  |
| Object                      | yes       |
| _[Conditions](#Conditions)_ | required  |
| `then`                      | required  |

## Basic usage

**If** is used with the [conditions](../testing/Assert%20that.md#conditions) defined
in [Assert that](../testing/Assert%20that.md) and if a condition holds, the
commands under `then` are executed

```yaml
Code example: Simple if

If:
  item: this
  equals: this
  then:
    Output: correct

Expected output: correct
```

## Multiple conditions

With multiple conditions, each one is evaluated.

```yaml
Code example: Multiple conditions in If

If:
  - item: one
    equals: one
    then:
      Output: 1

  - item: two
    equals: two
    then:
      Output: 2

Expected output:
  - 1
  - 2
```

If you only want to match one, use [When](When.md)

```yaml
Code example: Multiple conditions in When

When:
  - item: one
    equals: one
    then:
      Output: 1

  - item: two
    equals: two
    then:
      Output: 2

Expected output: 1
```