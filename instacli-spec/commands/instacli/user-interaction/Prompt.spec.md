# Command: Prompt

`Prompt` asks the user for input with an interactive prompt.

| Content type | Supported |
|--------------|-----------|
| Value        | no        |
| List         | no        |
| Object       | yes       |

[Prompt.schema.yaml](schema/Prompt.schema.yaml)

## Basic usage

With **Prompt**, you can ask the user a question.

<!-- answers
What is your name?: Hes
-->

```yaml instacli
Code example: Define input

Prompt: What is your name?

Print: Hello ${output}!
```

This will ask for user input on the command line:

```output
? What is your name? Hes
Hello Hes!
```

## Using a default value

Syntax for using a default value

<!-- answers
What is your name?: World
-->

```yaml instacli
Code example: Prompt with default value

Prompt:
  description: What is your name?
  default: World
```

The default value is a suggestion that is printed but can be overwritten by the user

```output
? What is your name? World
```

## Asking for a password

When asking for a password, the user prompt will mask the input that the user is typing in if you indicate it to have
`secret: true`.

<!-- answers
What is your password?: ssh
-->

```yaml instacli
Code example: Asking for a password

Prompt:
  description: What is your password?
  secret: true
```

will display as:

```output
? What is your password? ********
```

## Choosing from a list

You can ask for one item of a list:

<!-- answers
What is your favorite color?: Red
-->

```yaml instacli
Code example: Choose one item from a list

Prompt:
  description: What is your favorite color?
  enum:
    - Red
    - Green
    - Blue
```

The user can user the cursor keys to interactively select an item from a list, confirming the choice by hitting enter.

```output
? What is your favorite color? 
 ❯ ◉ Red
   ◯ Green
   ◯ Blue
```

Or multiple:

<!-- answers
What are your favorite colors?:
- Red
- Green
-->

```yaml instacli
Code example: Choose mutliple items from a list

Prompt:
  description: What are your favorite colors?
  enum:
    - Red
    - Green
    - Blue
  select: multiple
```

Here you can select the items you want by hitting the spacebar, before confirming with enter:

```output
? What are your favorite colors? 
 ❯ ◉ Red
   ◉ Green
   ◯ Blue
```

## Choosing an object

You can pass entire objects as choices into  `enum`. Then you need to specify the field that will be used to select the
object with the `display property` field. The entire object will be given as output.

<!-- answers
Select a user: Alice
-->

```yaml instacli
Code example: Choose an object

${users}:
  - name: Alice
    id: 123
  - name: Bob
    id: 456

Prompt:
  description: Select a user
  enum: ${users}
  display property: name

Print:
  You chose: ${output}
```

Here's an example to show how that works:

```output
? Select a user 
 ❯ ◉ Alice
   ◯ Bob

You chose:
  name: Alice
  id: 123
```

## Choosing only a field from an object

If you are only interested in a single field form an object, you can specify that with `value property`.

<!-- answers
Select a user: Alice
-->

```yaml instacli
Code example: Only use the value of a specific field when selecting from an enum list

${users}:
  - name: Alice
    id: 123
  - name: Bob
    id: 456

Prompt:
  description: Select a user
  enum: ${users}
  display property: name
  value property: id

Print:
  You chose: ${output}
```

Here's the result of that:

```output
? Select a user 
 ❯ ◉ Alice
   ◯ Bob

You chose: 123
```

## Conditions

Prompts support inline conditions. If the condition is false, the prompt is skipped.

```yaml instacli
Code example: Prompt with condition

Output: Already there

Prompt:
  description: What is the result?
  condition:
    empty: ${output}

Expected output: Already there

```

