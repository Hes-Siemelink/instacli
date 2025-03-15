# Command: Sort

`Sort` sorts an array.

| Content type | Supported |
|--------------|-----------|
| Value        | no        |
| List         | implicit  |
| Object       | yes       |

[Sort.schema.yaml](schema/Sort.schema.yaml)

## Basic usage

**Sort** expectes the list of things to sort in the `items` field. This should be a list of objects. The `by` parameter
specifies the field to sort on.

```yaml instacli
Code example: Sort a list of things

Sort:
  items:
    - name: March
      value: 3
    - name: February
      value: 2
    - name: January
      value: 1
  by: value

Expected output:
  - name: January
    value: 1
  - name: February
    value: 2
  - name: March
    value: 3
```

## Output chaining

If the ouput variable is set, you can leave out the `items` field.

```yaml instacli
Code example: Sort output

Output:
  - name: January
    value: 1
  - name: February
    value: 2
  - name: March
    value: 3

Sort:
  by: name

Expected output:
  - name: February
    value: 2
  - name: January
    value: 1
  - name: March
    value: 3
```