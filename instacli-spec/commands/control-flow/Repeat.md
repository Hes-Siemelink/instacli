# Command: When

`When` executes a single command from a list of conditions

| Name             | `Repeat` |
|------------------|----------|
| Value content    | no       |
| List content     | no       |
| Object content   | no       |
| Field (required) | Until    |

## Basic example

**Repeat** repeats a bunch of commands until the condition specified in `Until` is reached. See conditions in [Assert that]

```yaml
Code example: Count to five

Output: 0

Repeat:
  Add: 1
  Print: ${output}
  Until: 5
```

Prints:

    1
    2
    3
    4
    5
