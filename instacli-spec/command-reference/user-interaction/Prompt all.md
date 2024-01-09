# Command: Prompt all

`Promp all` is like [Prompt](Prompt.md), but asks mutlitple questions

| Content type | Supported |
|--------------|-----------|
| Value        | no        |
| List         | no        |
| Object       | yes       |

## Basic usage

Use **Prompt all** to fire multiple questions at once and capture the answers in the output variable.

<!-- run before code example
Stock answers:
  First name: Juan
  Last name: Pérez
-->

```yaml
Code example: Multiple questions

Prompt all:
  firstName: First name
  lastName: Last name

Print: Hello ${output.firstName} ${output.lastName}!
```

This will ask two questions and then print the result:

```commandline
? First name Juan
? Last name Pérez
Hello Juan, Pérez!
```

## Prompt properties

You can specify various properties: See [Prompt Properties](Prompt.md#prompt-properties)

* `description`: The question to ask the user.
* `default`: The default value
* `type`: The type of input: `select one`, `select multiple` or `password`
* `choices`: a list of objects to choose from. This will render a dropdown list when presented to the user.
* `display`: the field to display when passing a list of objects to `choices`
* `value`: if passing an object to `choices`, the result will be the value of this field and not the entire object

Here's an example:

<!-- run before code example
Stock answers:
  Email address: info@example.com
  Choose a color: Red
-->

```yaml
Code example: Prompt all with properties

Prompt all:
  email:
    description: Email address
    default: info@example.com
  color:
    description: Choose a color
    type: select one
    choices:
      - Red
      - Green
      - Blue
```