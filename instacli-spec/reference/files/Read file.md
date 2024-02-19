# Command: Read file

`Read file` loads Yaml.

| Content type | Supported                                        |
|--------------|--------------------------------------------------|
| Value        | yes                                              |
| List         | no                                               |
| Object       | no                                               |
| `realtive`   | the file to load, relative to the current script |

## Basic usage

Use **Read file** to load a Yaml / Json file

For example, we have a file `greeting.yaml` in the `samples/basic` directory:

```yaml file:greeting.yaml
greeting: Hello
language: en
```

Then you can read it like this:

```yaml instacli
Code example: Read Yaml from a file

Read file: samples/basic/greeting.yaml

Expected output:
  greeting: Hello
  language: en
```

<!--
Note: To make the automated test work, the file is actually read from the repository `samples` directory. 
-->

## Reading a file relative to your script

If you want to load a file that is next to your Instacli script, use the `relative` parameter:

```yaml instacli
Code example: Read a local file

Read file:
  relative: greeting.yaml

Expected output:
  greeting: Hello
  language: en    
```