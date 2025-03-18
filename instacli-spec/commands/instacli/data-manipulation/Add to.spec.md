# Command: Add to

With `Add to`, you can add stuff to variables in a concise way.

| Content type | Supported |
|--------------|-----------|
| Value        | no        |
| List         | implicit  |
| Object       | yes       |

[Add to.schema.yaml](schema/Add%20to.schema.yaml)

## Basic usage

Use **Add to** to add something to existing variables.

```yaml instacli
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

```yaml instacli
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

## Adding to a list

You can add an item to a list

```yaml instacli
Code example: Add an item to a list

${list}:
  - 1
  - 2

Add to:
  ${list}: 3

Assert equals:
  actual: ${list}
  expected:
    - 1
    - 2
    - 3
```

Or combine two lists.

```yaml instacli
Code example: Append a list to another

${list}:
  - 1
  - 2

Add to:
  ${list}:
    - 3
    - 4

Assert equals:
  actual: ${list}
  expected:
    - 1
    - 2
    - 3
    - 4
```

## Add to text

You can also extend a text string.

```yaml instacli
Code example: Append text

${text}: Hello

Add to:
  ${text}: " World"

Assert equals:
  actual: ${text}
  expected: Hello World
```

## Add to output

Use **[Append](Append.spec.md)** to add something to the `${output}` variable.

