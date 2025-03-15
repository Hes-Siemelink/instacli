# Command: Size

`Size` gives you the size of things

| Content type | Supported |
|--------------|-----------|
| Value        | yes       |
| List         | yes       |
| Object       | yes       |

[Size.schema.yaml](schema/Size.schema.yaml)

## Basic usage

Get the size of a list with Size

```yaml instacli
Code example: Size of a list

Size:
  - one
  - two
  - three

Expected output: 3
```

For texts, you get the number of characters:

```yaml instacli
Code example: Text size

Size: internationalization

Expected output: 20
```

And for objects, the number of entries

```yaml instacli
Code example: Object size

Size:
  1: one
  2: two

Expected output: 2
```

**Size** also works on numbers

```yaml instacli
Code example: Number size

Size: 12

Expected output: 12
```
