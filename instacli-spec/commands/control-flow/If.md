# Command: If

`If` exectues commands if a condition holds

| Name             | `If`       |
|------------------|------------|
| Value content    | no         |
| List content     | no         |
| Object content   | yes        |
| Field (required) | Conditions |
| Field (required) | `then`     |

## Basic example

**If** is used with the [conditions](../testing/Assert%20that.md#conditions) defined in [Assert that](../testing/Assert%20that.md) and if a condition holds, the
commands under `then` are executed

```yaml
Code example: Simple if

If:
  object: this
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
  - object: one
    equals: one
    then:
      Output: 1

  - object: two
    equals: two
    then:
      Output: 2

Expected output:
  - 1
  - 2
```

If you only want to match one, use [When]

```yaml
Code example: Multiple conditions in When

When:
  - object: one
    equals: one
    then:
      Output: 1

  - object: two
    equals: two
    then:
      Output: 2

Expected output: 1
```