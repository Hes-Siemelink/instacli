# Command: ${..} assignment

Set a variable value with a command in `${..}` syntax

| Content type | Supported |
|--------------|-----------|
| Value        | yes       |
| List         | yes       |
| Object       | yes       |

## Basic usage

Directly set the value of a variable, for example:

```yaml instacli
Code example: Set a variable value

${myvar}: Hello World!

Assert equals:
  actual: ${myvar}
  expected: Hello World!
```

## Use JSON path notation

When you only want a part of the output, you can't use [As](As.spec.md) to capture the variable. In that case, use the
`${..}:` assignment to indicate the part of the output that you want.

```yaml instacli
Code example: Get part of the output into a variable

Output:
  language: English
  greeting: Hello World!

${greeting}: ${output.greeting}

Assert equals:
  actual: ${greeting}
  expected: Hello World!
```
