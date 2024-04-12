# Command: Values

`Values` returns the list of values in an object, without the field names

| Content type | Supported |
|--------------|-----------|
| Value        | no        |
| List         | implicit  |
| Object       | yes       |

[Values.schema.yaml](schema/Values.schema.yaml)

## Basic usage

**Values** strips the object of its fields and returns a list of the contents.

```yaml instacli
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
