# Command: As

`As` sets a variable based on the `${out}` variable, that can be the output of a script

| Content type | Supported |
|--------------|-----------|
| Value        | yes       |
| List         | implicit  |
| Object       | no        |

## Basic usage

**As** takes the name of the variable to capture the `${out}` variable

```yaml
Code example: Assign a variable with As

Out: Hello World!
As: greeting

Assert equals:
  actual: ${greeting}
  expected: Hello World!
```

Note that the argument to **As** is not in `${..}` syntax.