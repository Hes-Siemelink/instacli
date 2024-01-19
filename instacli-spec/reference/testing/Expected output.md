# Command: Expected output

`Expected output` tests the `${output}` variable against a given value

| Content type | Supported |
|--------------|-----------|
| Value        | yes       |
| List         | yes       |
| Object       | yes       |

## Basic usage

**Expected output** is a shortcut for [Assert that](Assert%20that.md#object-equals) equality, comparing against `${output}`

It works for simple values

```yaml
Code example: Expect output to be a simpel value

Output: one

Expected output: one
```

Also for complex objects

```yaml
Code example: Compare output to complex value

Output:
  1: one
  2: two

Expected output:
  1: one
  2: two
```

And for lists

```yaml
Code example: Check output list

Output:
  - one
  - two

Expected output:
  - one
  - two
```
