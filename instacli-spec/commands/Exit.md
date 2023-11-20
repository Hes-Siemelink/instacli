# Command: Exit

`Exit` stops running the current script.

| Name           | `Exit` |
|----------------|--------|
| Value content  | yes    |
| List content   | yes    |
| Object content | yes    |

## Basic example

**Exit** is used to stop processing the current script. It will return the exit value to the calling script or to the shell.

```yaml
Code example: Exit from script

Exit: 0

Expected output: We are not supposed to get here
```