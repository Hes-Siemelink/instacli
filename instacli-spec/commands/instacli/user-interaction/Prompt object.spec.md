# Command: Prompt object

`Prompt object` is like [Prompt](Prompt.spec.md), but asks multiple questions

| Content type | Supported |
|--------------|-----------|
| Value        | no        |
| List         | no        |
| Object       | yes       |

[Prompt object.schema.yaml](schema/Prompt%20object.schema.yaml)

## Basic usage

Use **Prompt object** to fire multiple questions at once and capture the answers in the output variable.

<!-- answers
First name: Juan
Last name: Pérez
-->

```yaml instacli
Code example: Multiple questions

Prompt object:
  firstName: First name
  lastName: Last name

Print: Hello ${output.firstName} ${output.lastName}!
```

This will ask two questions and then print the result:

```output
? First name Juan
? Last name Pérez
Hello Juan Pérez!
```

## Prompt properties

You can specify various properties: See [Prompt Properties](Prompt.spec.md#prompt-properties)

* `description`: The question to ask the user.
* `default`: The default value
* `type`: The type of input: `select one`, `select multiple` or `password`
* `enum`: a list of objects to choose from. This will render a dropdown list when presented to the user.
* `display property`: the field to display when passing a list of objects to `enum`
* `value property`: if passing an object to `enum`, the result will be the value of this field and not the entire object

Here's an example:

<!-- answers
Email address: info@example.com
Choose a color: Red
-->

```yaml instacli
Code example: Prompt object with properties

Prompt object:
  email:
    description: Email address
    default: info@example.com
  color:
    description: Choose a color
    enum:
      - Red
      - Green
      - Blue
```

## Conditional questions

You can use the `condition` property to set a condition. If the condition is false, the question will be skipped and the
variable will not be set.

In the following example, there will only a prompt for variable a, and not for variable b

```yaml instacli
Code example: Prompt object, but not all

Answers:
  Value for a: Abracadabra
  Value for b: Borobudur

${switch}: a

Prompt object:

  a:
    description: Value for a
    condition:
      item: ${switch}
      equals: a

  b:
    description: Value for b
    condition:
      item: ${switch}
      equals: b

Expected output:
  a: Abracadabra

# Note: b is not set
```

## Depending questions

You can also make questions depend on previous questions. The answers to previous questions are available as variables
(within the scope of **Prompt object**). You can use those variables in conditions.

This example will prompt which variable to set, and depending on the result will ask the following question a or b.

```yaml instacli
Code example: Prompt object, with conditions

Answers:
  Choose which variable to set, a or b: a
  Value for a: Abracadabra
  Value for b: Borobudur

Prompt object:

  switch: Choose which variable to set, a or b

  a:
    description: Value for a
    condition:
      item: ${switch}  # Refers to the answer to the first question
      equals: a

  b:
    description: Value for b
    condition:
      item: ${switch}
      equals: b

Expected output:
  switch: a
  a: Abracadabra
```
