# Command: Stock answers

`Stock answers` prerecords answers for prompts, so they can pass automated tests.

| Content type | Supported |
|--------------|-----------|
| Value        | no        |
| List         | no        |
| Object       | yes       |

## Basic usage

Automated tests will hang on user interaction. With **Stock answers**, you can define the canned response so the tests can pass.
Note: **Stock answers** is only enabled in a test context.

To give an answer, simply define the question and answer under **Stock answers** like this:

```yaml
Code example: Prerecord an answer to a prompt

Stock answers:
  What is your name?: Alice

Prompt: What is your name?

Expected output: Alice
```
