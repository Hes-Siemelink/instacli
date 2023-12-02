# Command: Add

`Add` adds an item to something else

| Content type | Supported |
|--------------|-----------|
| Value        | no        |
| List         | implicit  |
| Object       | yes       |

## Basic usage

Use **Add** to combine two things

```yaml
Code example: Add a field

Add:
  to:
    1: one
    2: two
  item:
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

Add:
  to:
    - 1
    - 2
  item: 3

Expected output:
  - 1
  - 2
  - 3
```

Or combine two lists.

```yaml
Code example: Append a list to another

Add:
  to:
    - 1
    - 2
  item:
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

Add:
  to: Hello
  item: " World"

Expected output: Hello World
```
