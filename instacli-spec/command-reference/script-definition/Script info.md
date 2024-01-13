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

When running Instacli from the command line with the `cli` command, this is the description that is given.
For example, when listing the commands in a directory

```commandline
$ cli script-info-samples 
Script info usage examples

* Available commands: 
 > basic   A script containing a code example
```

## Definition of input parameters

If the script uses input parameters, use the long form of **Script info** to define them.

<!-- run before code example
${input}:
   name: world
-->

```yaml
Code example: Script info with input

Script info:
  description: A script with input parameters
  input:
    name: The name to greet

Print: Hello, ${input.name}!
```

Use `cli --help` to see the description of the parameters

```commandline
$ cli --help script-info-samples/input
A script with input parameters

Input parameters:
  --name       The name to greet
```
