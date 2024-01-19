# Command: Print

`Print` just prints stuff

| Content type | Supported |
|--------------|-----------|
| Value        | yes       |
| List         | yes       |
| Object       | yes       |

## Basic usage

Use **Print** to print a message to the console

```yaml
Code example: Print a message

Print: Hello, World!
```

## Printing Yaml

Object content is printed as Yaml

```yaml
Code example: Print Yaml

Print: 
  greeting: Hello, World!
```

may print

    greeting: "Hello, World!"

as that is equivalent Yaml.