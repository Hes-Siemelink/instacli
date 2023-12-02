# Command: Replace

`Replace` does a text find & replace.

| Content type   | Supported |
|----------------|-----------|
| Value          | yes       |
| List           | yes       |
| Object         | yes       |
| `in`           | required  |
| `find`         | required  |
| `replace with` | required  |

## Basic usage

Use **Replace** to modify text

```yaml
Code example: Replace in text

Replace:
  in: Hello me
  find: me
  replace with: World!

Expected output: Hello World!
```

This also works when in lists

```yaml
Code example: Replace in list

Replace:
  in:
    - one
    - two
    - three
  find: o
  replace with: a

Expected output:
  - ane
  - twa
  - three
```

And in objects

```yaml
Code example: Replace in object

Replace:
  in:
    greeting: Hello me
  find: me
  replace with: World!

Expected output:
  greeting: Hello World!
```