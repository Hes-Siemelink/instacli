# Command: Prompt

`Prompt` asks the user for input with an interactive prompt.

| Content type | Supported |
|--------------|-----------|
| Value        | no        |
| List         | no        |
| Object       | yes       |

## Basic usage

With **Prompt**, you can ask the user a question.

<!-- run before
Stock answers:
  What is your name?: Hes
-->

```yaml cli
Code example: Define input

Prompt: What is your name?

Print: Hello ${output}!
```

This will ask for user input on the command line:

```commandline
? What is your name? Hes
Hello Hes!
```

## Prompt properties

You can specify various properties:

* `description`: The question to ask the user.
* `default`: The default value
* `type`: The type of input: `select one`, `select multiple` or `password`
* `choices`: a list of objects to choose from. This will render a dropdown list when presented to the user.
* `display`: the field to display when passing a list of objects to `choices`
* `value`: if passing an object to `choices`, the result will be the value of this field and not the entire object

## Using a default value

Syntax for using a default value

<!-- run before
Stock answers:
  What is your name?: Hes
-->

```yaml cli
Code example: Prompt with default value

Prompt:
  description: What is your name?
  default: World
```

The default value is a suggestion that is printed but can be overwritten by the user

```commandline
? What is your name? World_
```

## Asking for a password

When asking for a password, the user prompt will mask the input that the user is typing in.

<!-- run before
Stock answers:
  What is your secret?: ssh
-->

```yaml cli
Code example: Asking for a password

Prompt:
  description: What is your secret?
  type: password
```

will display as:

```commandline
? What is your secret? ***
```

## Choosing from a list

You can ask for one item of a list:

<!-- run before
Stock answers:
  What is your favorite color?: Red
-->

```yaml cli
Code example: Choose one item from a list

Prompt:
  description: What is your favorite color?
  type: select one
  choices:
    - Red
    - Green
    - Blue
```

The user can user the cursor keys to interactively select an item from a list, confirming the choice by hitting enter.

```commandline
? What is your favorite color? 
 ❯ Red
   Green
   Blue
```

Or multiple:

<!-- run before
Stock answers:
  What are your favorite colors?:
    - Red
    - Green
-->

```yaml cli
Code example: Choose mutliple items from a list

Prompt:
  description: What are your favorite colors?
  type: select multiple
  choices:
    - Red
    - Green
    - Blue
```

Here you can select the items you want by hitting the spacebar, before confirming with enter:

```commandline
? What are your favorite colors? 
   ◉ Red
 ❯ ◉ Green
   ◯ Blue
```

## Choosing an object

You can pass entire objects as choices. Then you need to specify the field that will be used to select the object with
the `display` property. The entire object will be given as output.

<!-- run before
Stock answers:
  Select a user: Alice
-->

```yaml cli
Code example: Choose an object

${users}:
  - name: Alice
    id: 123
  - name: Bob
    id: 456

Prompt:
  description: Select a user
  type: select one
  choices: ${users}
  display: name

Print:
  You chose: ${output}
```

Here's an example to show how that works:

```commandline
? Select a user Alice
You chose:
  name: Alice
  id: 123
```

If you are only interested in a single field form an object, you can specify that with the `value` property

<!-- run before
Stock answers:
  Select a user: Alice
-->

```yaml cli
Code example: Only use a value when choosing an object

${users}:
  - name: Alice
    id: 123
  - name: Bob
    id: 456

Prompt:
  description: Select a user
  type: select one
  choices: ${users}
  display: name
  value: id

Print:
  You chose: ${output}
```

Here's the result of that:

```commandline
? Select a user Alice
You chose: 123
```
