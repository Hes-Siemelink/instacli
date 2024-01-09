# Command: Input

`Input` defines the input parameters of a script.

| Content type | Supported |
|--------------|-----------|
| Value        | no        |
| List         | no        |
| Object       | yes       |

## Basic usage

With **Input**, you define the input parameters of a script. When running in interactive mode, Instacli will prompt the
use for any input parameter that has not
been passed to the script.

<!-- run before code example
${input}:
   name: world
-->

```yaml
Code example: Define input

Input:
  name: What is your name?

Print: Hello, ${input.name}!
```

When running this, there are three possibilities

1. The variable `${name}` is defined. In that case all is good and nothing happens.
2. The variable `${name}` is not defined, and the script is run in interactive mode. Then the user is prompted with the
   questions **What is your name?** and the
   result is stored in the `${name}` variable.
3. The variable `${name}` is not defined, and the script is not run in interactive mode. Then an error is thrown and the
   script is aborted.

## Multiple variables

You can define multiple input parameters at once.

<!-- run before code example
${input}:
   greeting: Hello
   name: world
-->

```yaml
Code example: Define input with multiple variables

Input:
  greeting: What is your greeting?
  name: What is your name?

Print: ${input.greeting}, ${input.name}!
```

## More properties

You can specify various properties on the input parameters.

* `description`: The question to ask the user.
* `default`: The default value
* `type`: The type of input: `select one`, `select multiple` or `password`
* `choices`: a list of objects to choose from. This will render a dropdown list when presented to the user.
* `display`: the field to display when passing a list of objects to `choices`
* `value`: if passing an object to `choices`, the result will be the value of this field and not the entire object

For example:

```yaml
Code example: Input with default value

Input:
  name:
    description: What is your name?
    default: World
```

See [Prompt Properties](../user-interaction/Prompt.md#prompt-properties) for a full description.