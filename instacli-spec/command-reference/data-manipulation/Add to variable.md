# Command: Add to

With `Add to`, you can add stuff to variables in a concise way.

| Content type | Supported |
|--------------|-----------|
| Value        | no        |
| List         | implicit  |
| Object       | yes       |

## Basic usage

Use **Add to** to add something to existing variables.

```yaml
Code example: Add to variable

${index}: 0

Add to:
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

Add to:
  ${index}: 1
  ${offset}: 50

Assert equals:
  - actual: ${index}
    expected: 1
  - actual: ${offset}
    expected: 50
```

## Add to output

Use **Add to** with the `${output}` variable to append something to the current output

```yaml
Code example: Add a field

Output:
  1: one
  2: two

Add to:
  ${output}:
    3: three

Expected output:
  1: one
  2: two
  3: three
```

## Adding to a list

You can add an item to a list

```yaml
Code example: Add an item to a list

Output:
  - 1
  - 2

Add to:
  ${output}: 3

Expected output:
  - 1
  - 2
  - 3
```

Or combine two lists.

```yaml
Code example: Append a list to another

Output:
  - 1
  - 2

Add to:
  ${output}:
    - 3
    - 4

Expected output:
  - 1
  - 2
  - 3
  - 4
```

## Add to text

You can also extend a text string.

```yaml
Code example: Append text

Output: Hello

Add to:
  ${output}: " World"

Expected output: Hello World
```
