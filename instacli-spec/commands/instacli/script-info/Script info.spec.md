# Command: Script info

`Script info` contains the description of a script and the definition of the input.

| Content type | Supported |
|--------------|-----------|
| Value        | yes       |
| List         | no        |
| Object       | yes       |

[Script info.schema.yaml](schema/Script%20info.schema.yaml)

## Basic usage

With **Script info** you give a script a description.

There should be only one **Script info** command in a file, and it should be in top, so you can easily read it when
opening the file.

The simplest way of using **Script info** takes only text.

```yaml instacli
Code example: Basic Script info

Script info: A script containing a code example

# Do some stuff
```

When running Instacli from the command line with the `cli` command, this is the description that is given. For example,
when listing the commands in a directory

```
$ cli script-info-samples 
Script info usage examples

* Available commands: 
 > basic   A script containing a code example
```

You can also put the description in the `description` property:

```yaml instacli
Code example: Script info with description property

Script info:
  description: A script containing a code example
```

You will need this when specifying input parameters, see below.

## Definition of input parameters

If the script uses input parameters, you can define them in **Script info** with the `input` property. They will be
exposed as variables in the script.

<!-- yaml instacli
${input}:
 name: world
-->

 ```yaml instacli
  Code example: Script info with input

  Script info:
    input:
      name: The name to greet

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

```
$ cli --help script-info-samples/input
A script with input parameters

Input parameters:
  --name       The name to greet
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

Script info:
  input:
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

Script info:
  input:
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

Script info:
  input:
    name:
      description: What is your name?
      default: World
```

See [Prompt Properties](../user-interaction/Prompt.spec.md#prompt-properties) for a full description.

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

Script info:
  description: A script with a choice
  input:

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

### Hidden commands

When invoking Instacli interactively, `cli --help` will show the contents of the directory as commands. If you don't
want to expose a script this way, for example a helper script, then you can hide it with the `hide` property in **Script
info**.

For example, consider the file `helper.cli`:

```yaml file=helper.cli
Script info:
  description: Helper script
  hidden: true

Output: Something useful
```

It is not included in the directory listing:

```shell cli
cli --help .
```

```output
No commands available.
```

### Instacli version

You can indicate the version of the Instacli spec that the script is using.

```yaml instacli
Script info:
  instacli-spec: v0.1
```

## Using types

You can define the input and output of a Script as types.

First you need to define the types in the file, and then you can use them in the script. Types are defined in the file
`types.yaml`, in the same directory as the script.

```yaml file=types.yaml
FullName:
  base: object
  properties:
    first_name:
      description: First name
      type: string
    last_name:
      description: Last name
```

Then you can use the types in the script:

<!-- yaml instacli
${input}:
  first_name: Alice
  last_name: Wonderland
-->

```yaml instacli
Code example: Define script input and output with types

Script info:
  description: Get name details
  input type: FullName
```

<!-- yaml instacli
Output: Hello, ${input.first_name} ${input.last_name}

Expected output: Hello, Alice Wonderland
-->

