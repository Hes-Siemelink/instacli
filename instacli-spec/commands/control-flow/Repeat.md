# Command: When

`When` executes a single command from a list of conditions

| Content type                         | Supported |
|--------------------------------------|-----------|
| Value                                | no        |
| List                                 | implicit  |
| Object                               | yes       |
| _Actions_                            | optional  |
| `until`: _[Conditions](#Conditions)_ | required  |

## Usage

**Repeat** repeats a bunch of commands until the condition specified in `Until` is reached. See [conditions](../testing/Assert%20that.md#conditions)
in [Assert that](../testing/Assert%20that.md)

```yaml
Code example: Count to five

Output: 0

Repeat:
  Add: 1
  Print: ${output}
  until: 5
```

Prints:

    1
    2
    3
    4
    5
