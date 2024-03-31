# Command: Save as

`Save as` saves output to a file.

| Content type | Supported |
|--------------|-----------|
| Value        | yes       |
| List         | no        |
| Object       | no        |

## Basic usage

Use **Save as** store the contents of the `${output}` variable in a file.

```yaml instacli
Code example: Save output

Output: |
  Hello, World!

Save as: out/greeting.txt
```