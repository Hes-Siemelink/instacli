# Command: Read file

`Read file` loads Yaml from a file

| Content type | Supported |
|--------------|-----------|
| Value        | yes       |
| List         | no        |
| Object       | no        |

[Read file.schema.yaml](schema/Read%20file.schema.yaml)

## Basic usage

Use **Read file** to load a Yaml / Json file

For example, we have a file `greeting.yaml` in the `instacli-spec/commands/instacli/files` directory:

```yaml file=greeting.yaml
greeting: Hello
language: en
```

Then you can read it like this:

```yaml instacli
Code example: Read Yaml from a file

Read file: instacli-spec/commands/instacli/files/greeting.yaml

Expected output:
  greeting: Hello
  language: en
```

## Reading a file in the same directory as your script

If you want to load a file that is next to your Instacli script, use the `resource` parameter:

```yaml instacli
Code example: Read a local file

Read file:
  resource: greeting.yaml

Expected output:
  greeting: Hello
  language: en    
```