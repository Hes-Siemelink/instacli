# Command: Fields

`Fields` returns the list of fields in an object

| Content type | Supported |
|--------------|-----------|
| Value        | no        |
| List         | implicit  |
| Object       | yes       |

## Basic usage

**Fields** strips the fields (keys) of an object

```yaml script
Code example: Just the fields

Fields:
  1: gold
  2: dreams
  3: strawberries

Expected output:
  - "1"
  - "2"
  - "3"
```

Note that field names are always strings.