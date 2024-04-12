# Command: Replace

Does a text-based find&replace.

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
  replace with: World!

Expected output: Hello World!
```

Replace also works when in lists

```yaml instacli
Code example: Replace in list

Replace:
  text: o
  in:
    - one
    - two
    - three
  replace with: a

Expected output:
  - ane
  - twa
  - three
```

And in objects

```yaml instacli
Code example: Replace in object

Replace:
  text: me
  in:
    greeting: Hello me
  replace with: World!

Expected output:
  greeting: Hello World!
```