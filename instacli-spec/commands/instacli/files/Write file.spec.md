# Command: Write file

`Write file` saves output to a file.

| Content type | Supported |
|--------------|-----------|
| Value        | yes       |
| List         | no        |
| Object       | yes       |

[Write file.schema.yaml](schema/Write%20file.schema.yaml)

## Basic usage

Use **Write file** to store the contents of the `${output}` variable in a file.

```yaml instacli
Code example: Save output

Output: |
  Hello, World!

Write file: out/greeting.txt
```