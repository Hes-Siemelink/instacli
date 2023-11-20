# Command: When

`When` executes a single command from a list of conditions

| Name             | `When`     |
|------------------|------------|
| Value content    | no         |
| List content     | yes        |
| Object content   | no         |
| Field (required) | Conditions |
| Field (required) | `then`     |

## Basic example

**When** is a special case of **If**, working on a list of conditions (defined in [Assert that]).

**When** behaves different from **If**, because it will only execute the first matching conditions. When the condition holds, the commands under `then` are
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

If you only want to match one, use [When]

