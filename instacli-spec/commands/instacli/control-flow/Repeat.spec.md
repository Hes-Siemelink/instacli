# Command: Repeat

Executes a block of code until a condition is satisfied.

| Content type | Supported |
|--------------|-----------|
| Value        | no        |
| List         | implicit  |
| Object       | yes       |

[Repeat.schema.yaml](schema/Repeat.schema.yaml)

## Basic usage

**Repeat** repeats a bunch of commands until the condition specified in `Until` is reached.
See [conditions](../testing/Assert%20that.spec.md#conditions).

```yaml instacli
Code example: Count to five

Output: 1

Repeat:
  Print: ${output}

  Append: 1
  until: 6
```

Prints:

    1
    2
    3
    4
    5
