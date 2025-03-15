# Command: As

`As` sets a variable based on the `${output}` variable, that can be the output of a script

| Content type | Supported |
|--------------|-----------|
| Value        | yes       |
| List         | implicit  |
| Object       | no        |

[As.schema.yaml](schema/As.schema.yaml)

## Basic usage

**As** puts the `${output}` into a new variable

```yaml instacli
Code example: Assign a variable with As

Output: Hello World!
As: ${greeting}

Assert equals:
  actual: ${greeting}
  expected: Hello World!
```

Note that the argument to **As** is in `${..}` syntax but does not get expanded but populated instead.