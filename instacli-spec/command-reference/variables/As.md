# Command: As

`As` sets a variable based on the `${output}` variable, that can be the output of a script

| Content type | Supported |
|--------------|-----------|
| Value        | yes       |
| List         | implicit  |
| Object       | no        |

## Basic usage

**As** takes the name of the variable to capture the `${output}` variable

```yaml
Code example: Assign a variable with As

Output: Hello World!
As: greeting

Assert equals:
  actual: ${greeting}
  expected: Hello World!
```

Note that the argument to **As** is not in `${..}` syntax.