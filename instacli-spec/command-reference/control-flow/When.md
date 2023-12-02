# Command: When

`When` executes a single command from a list of conditions

| Content type                | Supported |
|-----------------------------|-----------|
| Value                       | no        |
| List                        | yes       |
| Object                      | no        |
| _[Conditions](#Conditions)_ | required  |
| `then`                      | required  |

## Basic usage

**When** is a special case of [If](If.md), working on a list of [conditions](../testing/Assert%20that.md#conditions) defined
in [Assert that](../testing/Assert%20that.md).

**When** behaves different from **If**, because it will only execute the first matching condition. When that condition holds, the commands under `then` are
executed.

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

If you want all matching conditions to evaluated, use **If**:

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
