# Command: Script info

`Script info` contains the description of a script and the definition of the input.

| Content type  | Supported                              |
|---------------|----------------------------------------|
| Value         | yes                                    |
| List          | no                                     |
| Object        | yes                                    |
| `description` | The description of the script          |
| `input`       | The definition of the input parameters |

## Basic usage

With **Script info** you give a script a description.

There should be only one **Script info** command in a file, and it should be in top, so you can easily read it when
opening the file.

The simplest way of using **Script info** takes only text.

```yaml
Code example: Basic Script info

Script info: A script containing a code example

# Do some stuff
```

When running Instacli from the command line with the `cli` command, this is the description that is given. For example,
when listing the commands in a directory

```commandline
$ cli script-info-samples 
Script info usage examples

* Available commands: 
 > basic   A script containing a code example
```

You can also put the description in the `description` property:

```yaml
Code example: Script info with description property

Script info:
  description: A script containing a code example
```

You will need this when specifying input parameters, see below.

## Definition of input parameters

If the script uses input parameters, you can define them in **Script info** with the `input` property.

<!-- run before
${input}:
   name: world
-->

```yaml
Code example: Script info with input

Script info:
  input:
    name: The name to greet

Print: Hello, ${input.name}!
```

When running this, there are three possibilities

1. The variable `${name}` is defined. In that case all is good and nothing happens.
2. The variable `${name}` is not defined, and the script is run in interactive mode. Then the user is prompted with the
   questions **What is your name?** and the result is stored in the `${name}` variable.
3. The variable `${name}` is not defined, and the script is not run in interactive mode. Then an error is thrown and the
   script is aborted.

## Cli help

Use `cli --help` to see the description of the parameters

```commandline
$ cli --help script-info-samples/input
A script with input parameters

Input parameters:
  --name       The name to greet
```

## Multiple variables

You can define multiple input parameters at once.

<!-- run before
${input}:
   greeting: Hello
   name: world
-->

```yaml
Code example: Define input with multiple variables

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
* `choices`: a list of objects to choose from. This will render a dropdown list when presented to the user.
* `display`: the field to display when passing a list of objects to `choices`
* `value`: if passing an object to `choices`, the result will be the value of this field and not the entire object

For example:

```yaml
Code example: Input with default value

Script info:
  input:
    name:
      description: What is your name?
      default: World
```

See [Prompt Properties](../user-interaction/Prompt.md#prompt-properties) for a full description.