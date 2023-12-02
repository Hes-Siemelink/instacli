# Command: Add to output

`Add to output` adds something to the `${output}` variable

| Content type | Supported |
|--------------|-----------|
| Value        | yes       |
| List         | yes       |
| Object       | yes       |

## Basic usage

Use **Add to output** to append something to the current output

```yaml
Code example: Add a field

Output:
  1: one
  2: two

Add to output:
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

Add to output: 3

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

Add to output:
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

Add to output: " World"

Expected output: Hello World
```
