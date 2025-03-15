# Command: Append

Use `Append` to add stuff to the output variable.

| Content type | Supported |
|--------------|-----------|
| Value        | no        |
| List         | implicit  |
| Object       | yes       |

[Append.schema.yaml](schema/Append.schema.yaml)

## Basic usage

Use **Append** to add something to `${output}`.

```yaml instacli
Code example: Add to output

Output:
  1: one
  2: two

Append:
  3: three

Expected output:
  1: one
  2: two
  3: three
```

## Adding to a list

You can also add to a list

```yaml instacli
Code example: Add a single item to a list

Output:
  - 1
  - 2

Append: 3

Expected output:
  - 1
  - 2
  - 3
```

Or combine two lists.

```yaml instacli
Code example: Append a list to another

Output:
  - 1
  - 2

Append:
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

```yaml instacli
Code example: Append text

Output: Hello

Append: " World"

Expected output: Hello World
```
