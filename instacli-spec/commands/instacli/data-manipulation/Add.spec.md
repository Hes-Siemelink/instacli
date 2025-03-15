# Command: Add

`Add` adds an item to something else

| Content type | Supported |
|--------------|-----------|
| Value        | no        |
| List         | implicit  |
| Object       | yes       |

[Add.schema.yaml](schema/Add.schema.yaml)

## Basic usage

Add some numbers

```yaml instacli
Code example: 1 + 1 = 2

Add:
  - 1
  - 1

Expected output: 2
```

## Add on objects

Use **Add** to combine two things

```yaml instacli
Code example: Add a field

Add:
  - 1: one
    2: two
  - 3: three

Expected output:
  1: one
  2: two
  3: three
```

## Adding to a list

You can add an item to a list

```yaml instacli
Code example: Add an item to a list

${list}:
  - 1
  - 2

Add:
  - ${list}
  - 3

Expected output:
  - 1
  - 2
  - 3
```

Or combine two lists.

```yaml instacli
Code example: Append a list to another

Add:
  - - 1
    - 2
  - - 3
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

Add:
  - Hello
  - " World"

Expected output: Hello World
```
