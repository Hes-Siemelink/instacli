# Command: ${..} assignment

Set a variable value with a command in `${..}` syntax

| Content type | Supported |
|--------------|-----------|
| Value        | yes       |
| List         | yes       |
| Object       | yes       |

## Basic usage

Directly set the value of a variable, for example:

```yaml
Code example: Set a variable value

${myvar}: Hello World!

Assert equals:
  actual: ${myvar}
  expected: Hello World!
```

## Use JSON path notation

When you only want a part of the output, you can't use [As](As.md) to capture the variable. In that case, use `${..}` to
get only part of the output.

```yaml
Code example: Get part of the output into a variable

Out:
  greeting: Hello World!

${myvar}: ${out.greeting}

Assert equals:
  actual: ${myvar}
  expected: Hello World!
```
