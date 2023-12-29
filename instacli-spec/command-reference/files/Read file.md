# Command: Read file

`Read file` loads Yaml.

| Content type | Supported |
|--------------|-----------|
| Value        | yes       |
| List         | no        |
| Object       | no        |

## Basic usage

Use **Read file** to load a Yaml / Json file

Suppose you have a file `sample.yaml`:

```yaml file:sample.yaml
greeting: Hello
language: en
```

Then you can read it like this:

```yaml
Code example: Read Yaml from a file

Read file: sample.yaml

Expected output:
    greeting: Hello
    language: en
```