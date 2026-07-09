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

<!-- answers
Do you want to continue?: "Yes"
-->

```yaml instacli
Code example: Simple confirmation message

Confirm: Do you want to continue?

Print: Thank you for confirming!
```

This will ask for user input on the command line:

```output
? Do you want to continue? 
 ❯ ◉ Yes
   ◯ No

Thank you for confirming!
```

## Handling rejection

When a user says no, the **Confirm** command will raise an error. You can catch this error with an `On error` block.

<!-- answers
Are you sure?: "No"
-->

```yaml instacli
Code example: Not confirmed

Confirm: Are you sure?

On error:
  Exit: Not confirmed

Expected output: Script will not reach this point
```

# Non-interactive mode

When running in non-interactive mode, **Confirm** commands are skipped silently.

Suppose you have the file `headless.cli`:

```yaml file=headless.cli
Confirm: Are you there?

Print: Thank you for NOT confirming!
```

If you run it headless you will not get the confirmation prompt.

```shell cli
cli --non-interactive headless.cli
```

will print

```output
Thank you for NOT confirming!
```
