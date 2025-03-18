# Command: Expected output

`Expected output` tests the `${output}` variable against a given value

| Content type | Supported |
|--------------|-----------|
| Value        | yes       |
| List         | yes       |
| Object       | yes       |

[Expected output.schema.yaml](schema/Expected%20output.schema.yaml)

## Basic usage

**Expected output** is a shortcut for [Assert that](Assert%20that.spec.md#object-equals) equality, comparing against
`${output}`

It works for simple values

```yaml instacli
Code example: Expect output to be a simple value

Output: one

Expected output: one
```

Also for complex objects

```yaml instacli
Code example: Compare output to complex value

Output:
  1: one
  2: two

Expected output:
  1: one
  2: two
```

And for lists

```yaml instacli
Code example: Check output list

Output:
  - one
  - two

Expected output:
  - one
  - two
```
