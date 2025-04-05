# Command: Write file

`Write file` saves output to a file.

| Content type | Supported |
|--------------|-----------|
| Value        | yes       |
| List         | no        |
| Object       | yes       |

[Write file.schema.yaml](schema/Write%20file.schema.yaml)

## Writing content to a file

Use **Write file** to store content in a file

```yaml instacli
Code example: Write content to a file

Write file:
  file: out/greeting.txt
  content: Hello, World!

Read file: out/greeting.txt

Expected output: Hello, World!
```

## Short version

You can store the contents of the `${output}` variable directly with the short form.

```yaml instacli
Code example: Save output

Output: Hello, World!

Write file: out/greeting.txt
```

