# Command: Task

`Task` marks the start of a logical section in a script. It doesn't do anything by itself, but you can use it to put group boundaries in your script.

| Content type | Supported |
|--------------|-----------|
| Value        | yes       |
| List         | no        |
| Object       | no        |

## Basic usage

**Task** indicates a logical grouping of commands

<!-- run before code example
Stock answers:
  What is your name?: Alicia
  What is your favorite ice cream flavor?: strawberry
-->

```yaml
Code example: Use Task for grouping

Task: Get user input

Prompt: What is your name?
As: name

---
Prompt: What is your favorite ice cream flavor?
As: favorite

---
Task: Process results

Print: Hello ${name}, you like ${favorite} ice cream.
```