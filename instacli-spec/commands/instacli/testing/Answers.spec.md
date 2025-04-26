# Command: Answers

`Answers` prerecords answers for prompts, so they can pass automated tests.

| Content type | Supported |
|--------------|-----------|
| Value        | no        |
| List         | no        |
| Object       | yes       |

[Answers.schema.yaml](schema/Answers.schema.yaml)

## Basic usage

Automated tests will hang on user interaction. With **Answers**, you can define the canned response so the tests can
pass.

To give an answer, simply define the question and answer under **Answers** like this:

```yaml instacli
Code example: Prerecord an answer to a prompt

Answers:
  What is your name?: Alice

Prompt: What is your name?

Expected output: Alice
```
