# Command: When

`When` executes a single command from a list of conditions

| Content type | Supported |
|--------------|-----------|
| Value        | no        |
| List         | yes       |
| Object       | no        |

[When.schema.yaml](schema/When.schema.yaml)

## Basic usage

**When** is a special case of [If](If.spec.md), working on a list
of [conditions](../testing/Assert%20that.spec.md#conditions)
defined in [Assert that](../testing/Assert%20that.spec.md).

**When** behaves different from **If**, because it will only execute the first matching condition. When that condition
holds, the commands under `then` are executed but the rest of the conditions are skipped.

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

The conditiona in **When** statements should only have an `if` but not an `else`. You can add an `else` at the end of
the list though:

```yaml instacli
Code example: When with else

When:
  - item: one
    equals: 1
    then:
      Output: 1

  - item: two
    equals: 2
    then:
      Output: 2

  - else:
      Output: no match

Expected output: no match
```

If you want all matching conditions to evaluated, use **[If](If.spec.md)**:

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
