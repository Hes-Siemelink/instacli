# Command: Add to variable

With `Add to variable`, you can add stuff to variables in a concise way.

| Content type | Supported |
|--------------|-----------|
| Value        | no        |
| List         | implicit  |
| Object       | yes       |

## Basic usage

Use **Add to variable** to add something to existing variables.

```yaml
Code example: Add to variable

${index}: 0

Add to variable:
  ${index}: 1

Assert equals:
  actual: ${index}
  expected: 1
```

## Multiple variables

You can do this with multiple variables at once

```yaml
Code example: Add to multiple variables

${index}: 0
${offset}: 0

Add to variable:
  ${index}: 1
  ${offset}: 50

Assert equals:
  - actual: ${index}
    expected: 1
  - actual: ${offset}
    expected: 50
```

## What can be added to what

See [Add to output](Add%20to%20output.md) for more details. **Add to variable** works in the same way. 