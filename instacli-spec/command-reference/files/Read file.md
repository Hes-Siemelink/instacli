# Command: Read file

`Read file` loads Yaml.

| Content type | Supported |
|--------------|-----------|
| Value        | yes       |
| List         | no        |
| Object       | no        |

## Basic usage

Use **Read file** to load a Yaml / Json file

Suppose you have a file `samples/greeting.yaml`:

```yaml file:greeting.yaml
greeting: Hello
language: en
```

Then you can read it like this:

```yaml
Code example: Read Yaml from a file

Read file: samples/greeting.yaml

Expected output:
  greeting: Hello
  language: en
```

<!--
Note: To make the automated test work, the file is actually read from the repository `samples` directory. 
-->