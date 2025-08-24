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

## Hidden commands

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

## Instacli version

You can indicate the version of the Instacli spec that the script is using.

```yaml instacli
Script info:
  instacli-spec: 0.5.1
```

## Script input

You can define the input of a Script with the `input` property. This is an old way of defining input parameters. Use
the [Input parameters](Input%20parameters.spec.md) command instead.

### Using types

As an alternative, you can define the input and output of a Script as types.

> [!NOTE]
> This is an experimental feature, and may change in future versions of Instacli.

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

