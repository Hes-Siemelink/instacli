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
Test case: Count to ten

Output: 0

Repeat:
  Add: 1
  Print: ${output}
  Until:
    object: ${output}
    equals: 10

```