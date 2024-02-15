# Command: ${..} assignment

Set a variable value with a command in `${..}` syntax

| Content type | Supported |
|--------------|-----------|
| Value        | yes       |
| List         | yes       |
| Object       | yes       |

## Basic usage

Directly set the value of a variable, for example:

```yaml script
Code example: Set a variable value

${myvar}: Hello World!

Assert equals:
  actual: ${myvar}
  expected: Hello World!
```

## Use JSON path notation

When you only want a part of the output, you can't use [As](As.md) to capture the variable. In that case, use `${..}` to
get only part of the output.

```yaml script
Code example: Get part of the output into a variable

Output:
  greeting: Hello World!

${myvar}: ${output.greeting}

Assert equals:
  actual: ${myvar}
  expected: Hello World!
```
