# Command: Replace

Does a text-based find & replace.

| Content type | Supported |
|--------------|-----------|
| Value        | yes       |
| List         | yes       |
| Object       | yes       |

[Replace.schema.yaml](schema/Replace.schema.yaml)

## Basic usage

Use **Replace** to modify text

```yaml instacli
Code example: Replace in text

Replace:
  text: me
  in: Hello me
  with: World!

Expected output: Hello World!
```

Replace also works in lists

```yaml instacli
Code example: Replace in list

Replace:
  text: o
  in:
    - one
    - two
    - three
  with: a

Expected output:
  - ane
  - twa
  - three
```

And with objects

```yaml instacli
Code example: Replace in object

Replace:
  text: me
  in:
    greeting: Hello me
  with: World!

Expected output:
  greeting: Hello World!
```

## Modifying output variable

By omitting the `in` field, you can modify the output variable directly. This is useful to filter and modify the output
of a previous command.

```yaml instacli
Code example: Replace in text

Output: Hello World!

Replace:
  text: World!
  with: Alice

Expected output: Hello Alice
```