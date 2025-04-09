# Command: Confirm

`Confirm` asks the user for confirmation on a single topic

| Content type | Supported |
|--------------|-----------|
| Value        | yes       |
| List         | no        |
| Object       | no        |

[Confirm.schema.yaml](schema/Confirm.schema.yaml)

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

```shell
? Do you want to continue? Yes
Thank you for confirming!
```

## Handling rejection

When a user says no, the **Confirm** command will raise on error. You can catch this error with an `On error` block.

<!-- yaml instacli before
Stock answers:
  Are you sure?: "No"
-->

```yaml instacli
Code example: Not confirmed

Confirm: Are you sure?

On error:
  Exit: Not confirmed

Expected output: Script will not reach this point
```
