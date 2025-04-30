# Command: <any instacli file in the same directory>

You can run any instacli file in the same directory as a regular command

| Content type | Supported |
|--------------|-----------|
| Value        | no        |
| List         | implicit  |
| Object       | yes       |

## Basic usage

Suppose you have a cli file `generate-greeting.cli`

```yaml file=generate-greeting.cli
Output: Hello ${input.name}!
```

Then you can call it from another Instacli file. Convert "skewer-case" to "Sentence case" and off you go.

```yaml instacli
Code example: Call another instacli file as a command

Generate greeting:
  name: Alice

Expected output: Hello Alice!
```

When you specify a field, it will be passed as part of the `${input}` variable in the script that you call. For example,
the value for `name`, Alice, will be available as `${input.name}` in the target script.   
