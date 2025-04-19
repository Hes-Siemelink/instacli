# Command: Do

Use `Do` to execute one or more other commands.

| Content type | Supported |
|--------------|-----------|
| Value        | no        |
| List         | implicit  |
| Object       | yes       |

[Do.schema.yaml](schema/Do.schema.yaml)

## Basic usage

Remember that the **Instacli** syntax is in Yaml, so it is not permitted to repeat a key. This poses a problem in a
programming language, wher you are prone to have several invvocations of the same command, but with different
parameters.

There are multiple ways to execute various commands with the same name in Instacli.

First there, is **Do**, that takes a list of commands to execute:

```yaml instacli
Code example: Use 'Do' to repeat a command

Do:
  - Print: Hello
  - Print: World!
```

This will give the following output:

```output
Hello
World!
```

**Do** makes use of a nifty Instacli feature: if a command is defined to take object or value content, but not a list,
you can pass it a list of things, and they will be executed sequentially with the same command. The above example can be
written more concisely as:

```yaml instacli
Code example: List-expanding invocation

Print:
  - Hello
  - World!
```

In fact, the definition of **Do** specifies that it only takes one Command object as a child. Using it with a list, as
in the above example, is just a case of the list-expanding invocation.

The final option is just to stick `---` somewhere in the code to separate the yaml maps. Instacli will stitch them
together. This only works on the top-level of course.

```yaml instacli
Code example: Use separators

Print: Hello
---
Print: World!
```

## Capture the output

**Do** can also be useful if you have multiple commands and you want to capture the output in a list.

```yaml instacli
Code example: The result of Do as a list

Do:
  - Output: one
  - Output: two
  - Output: three

Expected output:
  - one
  - two
  - three
```