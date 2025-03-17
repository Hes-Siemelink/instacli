# Command: Print

`Print` just prints stuff

| Content type | Supported |
|--------------|-----------|
| Value        | yes       |
| List         | yes       |
| Object       | yes       |

[Print.schema.yaml](schema/Print.schema.yaml)

## Basic usage

Use **Print** to print a message to the console

```yaml instacli
Code example: Print a message

Print: Hello, World!
```

This will print

```output
Hello, World!
```

## Printing Yaml

Object content is printed as Yaml

```yaml instacli
Code example: Print Yaml

Print:
  greeting: Hello, World!
```

should print

    greeting: "Hello, World!"

or equivalent Yaml. The exact format may vary depending on the Yaml library used by the implementation.