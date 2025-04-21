# Command: Prompt

`Prompt` asks the user for input with an interactive prompt.

| Content type | Supported |
|--------------|-----------|
| Value        | no        |
| List         | no        |
| Object       | yes       |

[Prompt.schema.yaml](../commands/instacli/user-interaction/schema/Prompt.schema.yaml)

## Basic usage

Sometimes you need to validate the user input before proceeding with the script.

With **Prompt**, you can ask the user a question.

<!-- answers
What is your name?: Hes
-->

```yaml instacli
Code example: Define input

Repeat:
  Prompt: What is your name?

  until: Hes

Prompt:
  description: What is your name?
  validate script:
    Append: (last name)
  valid if:
    equals: Hes (last name)
    else:
      Print: Please enter Hes

Print: Hello ${output}!
```

