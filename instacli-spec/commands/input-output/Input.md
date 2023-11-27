# Command: Input

`Input` defines the input parameters of a script.

| Content type | Supported |
|--------------|-----------|
| Value        | no        |
| List         | no        |
| Object       | yes       |

## Basic usage

With **Input** you define the input parameters of a script. When running in interactive mode, Instacli will prompt the use for any input parameter that has not
been passed to the script.

```yaml
${name}: world

---
Code example: Define input

Input:
  name: What is your name?

Print: Hello, ${name}!
```

When running this, there are three possibilities

1. The variable `${name}` is defined. In that case all is good and nothing happens.
2. The variable `${name}` is not defined, and the script is run in interactive mode. Then the user is prompted with the questions **What is your name?** and the
   result is stored in the `${name}` variable.
3. The variable `${name}` is not defined, and the script is not run in interactive mode. Then an error is thrown and the script is aborted.

## Multiple variables

You can define multiple input parameters at once.

```yaml
${greeting}: Hello
${name}: world

---
Code example: Define input

Input:
  greeting: What is your greeting?
  name: What is your name?

Print: ${greeting}, ${name}!
```

## More properties

You can specify various properties on the input parameters:

* `description`: The question to ask the user.
* `default`: The default value
* `type`: The type of input: `select one`, `select multiple` or `password`
* `choices`: a list of objects to choose from. This will render a dropdown list when presented to the user.
* `display`: the field to display when passing a list of objects to `choices`
* `value`: if passing an object to `choices`, the result will be the value of this field and not the entire object

## Using a default value

Syntax for using a default value

```yaml
Code example: Input with default value

Input:
  name:
    description: What is your name?
    default: World
```

## Asking for a password

When asking for a password, the user prompt will mask the input that the user is typing in.

```yaml
Code example: Asking for a password

Input:
  name:
    description: What is your secret?
    default: I don't have one
    type: password
```

## Choosing from a list

You can ask for one item of a list:

```yaml
Code example: Choose one item from a list

Input:
  color:
    description: What is your favorite color?
    type: select one
    choices:
      - Red
      - Green
      - Blue
    default: Red

Expected output:
  color: Red
```

Or multiple:

```yaml
Code example: Choose mutliple items from a list

Input:
  color:
    description: What are your favorite colors?
    type: select multiple
    choices:
      - Red
      - Green
      - Blue
    default:
      - Red
      - Green

Expected output:
  color:
    - Red
    - Green
```

## Choosing an object

When passing an objects as choices, you need to specify a field that will display the value with the `display` property.

```yaml
Code example: Choose an object

${users}:
  - name: Alice
    id: 123
  - name: Bob
    id: 456

Input:
  user:
    description: Select a user
    type: select one
    choices: ${users}
    display: name
    default:
      name: Alice
      id: 123

Expected output:
  user:
    name: Alice
    id: 123
```

If you are only interested in a single field form an object, you can speifyc that with the `value` property

```yaml
Code example: Only use a value when choosing an object

${users}:
  - name: Alice
    id: 123
  - name: Bob
    id: 456

Input:
  user:
    description: Select a user
    type: select one
    choices: ${users}
    display: name
    value: id
    default: 123

Expected output:
  user: 123
```
