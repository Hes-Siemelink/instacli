# Command: If

`If` exectues commands if a condition holds

| Content type | Supported |
|--------------|-----------|
| Value        | no        |
| List         | implicit  |
| Object       | yes       |

[If.schema.yaml](schema/If.schema.yaml)

## Basic usage

**If** is used with the [conditions](../testing/Assert%20that.spec.md#conditions) defined
in [Assert that](../testing/Assert%20that.spec.md) and if a condition holds, the commands under `then` are executed

```yaml instacli
Code example: Simple if

If:
  item: this
  equals: this
  then:
    Output: correct

Expected output: correct
```

You can also use `else`:

```yaml instacli
Code example: Simple if with else

If:
  item: this
  equals: that
  then:
    Output: correct
  else:
    Output: no match

Expected output: no match
```

## Multiple conditions

With multiple conditions, each one is evaluated.

```yaml instacli
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

If you only want to match one, use [When](When.spec.md)

```yaml instacli
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