# Command: Confirm

`Confirm` asks the user for confirmation on a single topic

| Content type | Supported |
|--------------|-----------|
| Value        | yes       |
| List         | no        |
| Object       | no        |

[Prompt.schema.yaml](schema/Prompt.schema.yaml)

## Basic usage

With **Confirmation**, you can ask the user a question.

<!-- yaml instacli before
Stock answers:
  Do you want to continue?: "Yes"
-->

```yaml instacli
Code example: Simple confirmation message

Confirm: Do you want to continue?

Print: Thank you for confirming!
```

This will ask for user input on the command line:

```commandline
? Do you want to continue? Yes
Thank you for confirming!
```