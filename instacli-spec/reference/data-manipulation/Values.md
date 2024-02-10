# Command: Values

`Values` returns the list of values in an object, without the field names

| Content type | Supported |
|--------------|-----------|
| Value        | no        |
| List         | implicit  |
| Object       | yes       |

## Basic usage

**Values** strips the object of its fields and returns a list of the contents.

```yaml cli
Code example: Only the values

Values:
  1: gold
  2: dreams
  3: strawberries

Expected output:
  - gold
  - dreams
  - strawberries
```
