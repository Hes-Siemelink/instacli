# Command: Input parameters

`Input parameters` contains the definitions of the input parameters for a script.

| Content type | Supported |
|--------------|-----------|
| Value        | no        |
| List         | no        |
| Object       | yes       |

[Input.parameters.yaml](schema/Input%parameters.schema.yaml)

## Basic usage

List the input parameters for a script using **Input parameters**

There should be only one **Input parameters** command in a file, and it should be on top, just below Script info, so you
can easily read it when opening the file.

The simplest way of using **Input parameters** takes a simple map, where the key is the name of the input parameter, and
the value is the description.

<!-- yaml instacli
${input}:
 name: world
-->

```yaml instacli
Code example: Basic Input parameter usage

Input parameters:
  name: Your name

Print: Hello, ${name}!
```

When running this, there are three possibilities

1. The variable `${name}` is provided as input. In that case all is good and nothing happens.
2. The variable `${name}` is not provided, and the script is run in interactive mode. Then the user is prompted with the
   questions **What is your name?** and the result is stored in the `${name}` variable.
3. The variable `${name}` is not provided, and the script is not run in interactive mode. Then an error is thrown and
   the script is aborted.

## Cli help

Use `cli --help` to see the description of the parameters

Let's put the above example in a file called `input.cli`:

```yaml file=input.cli
Script info: A script with input parameters

Input parameters:
  name: Your name

Print: Hello, ${name}!
```

Then running

```shell cli
cli --help input.cli
```

Should print:

```output
A script with input parameters

Options:
  --name   Your name
```

## Multiple variables

You can define multiple input parameters at once.

<!-- yaml instacli
${input}:
   greeting: Hello
   name: world
-->

```yaml instacli
Code example: Define input with multiple variables

Input parameters:
  greeting: What is your greeting?
  name: What is your name?

Print: ${greeting}, ${name}!
```

## The input variable

Input parameters are also stored in the `${input}` variable.

<!-- yaml instacli
${input}:
   greeting: Hello
   name: world
-->

```yaml instacli
Code example: Input with direct variable access

Input parameters:
  greeting: What is your greeting?
  name: What is your name?

Print: ${input.greeting}, ${input.name}!
```

## More properties

You can specify various properties on the input parameters.

* `description`: The question to ask the user.
* `default`: The default value
* `type`: The type of input: `select one`, `select multiple` or `password`
* `enum`: a list of objects to choose from. This will render a dropdown list when presented to the user.
* `display property`: the field to display when passing a list of objects to `enum`
* `value property`: if passing an object to `enum`, the result will be the value of this field and not the entire object

For example:

```yaml instacli
Code example: Input with default value

Input parameters:
  name:
    description: What is your name?
    default: World
```

See [Prompt Properties](../user-interaction/Prompt.spec.md) for a full description.

## Variables and conditions

You can define input depending on other input properties being set. The properties that are being referred to need to be
defined before the property that is using them. You can to them as part of the `${input}` variable, for example
`${input.otherVariable}`.

This example uses the ${input.switch} to determine which variable will be part of the input. By setting `switch` to `a`,
the `property-A` is set but not `property-B`.

<!-- yaml instacli
${input}: { }
-->

<!-- TODO Make this run in interactive mode, so we can use 'Answers' for a more compelling example. --> 

```yaml instacli
Code example: Script info with variables and conditions

Input parameters:

  switch:
    description: Choose a or b
    default: a

  property-A:
    description: What is the value for A?
    condition:
      item: ${input.switch}
      equals: a
    default: Ananas

  property-B:
    description: What is the value for B?
    condition:
      item: ${input.switch}
      equals: b
    default: Bologna

Assert equals:
  actual: ${input}
  expected:
    switch: a
    property-A: Ananas
```

