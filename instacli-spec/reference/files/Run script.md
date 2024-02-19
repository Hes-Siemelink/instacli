# Command: Run script

Use **Run script** to run another Instacli script. See also [Instacli files as commands](Instacli files as commands.md)

| Content type | Supported                                                |
|--------------|----------------------------------------------------------|
| Value        | no                                                       |
| List         | implicit                                                 |
| Object       | yes                                                      |
| `file`       | the Instacli file to run, in the current directory       |
| `realtive`   | the Instacli file to run, relative to the current script |
| `input`      | The input passed to the script                           |

## Basic usage

Suppose you have a cli file `create-greeting.cli`

```yaml file:create-greeting.cli
Output: Hello ${input.name}!
```

Then you can call it from another Instacli file using **Run script**.

```yaml instacli
Code example: Call another instacli file

Run script:
  relative: create-greeting.cli
  input:
    name: Alice
    default: World

Expected output: Hello Alice!
```

The values under  `${input}` will be passed as input to the script that you call. For example, the value for `name`,
Alice, will be available as `${input.name}` in the target script.

## Finding the script

In the example above, we used the property `relative` to indicate that the script to be called was in the same directory
as the current script.

Use the `file` property to look for a script in the directory that you are calling Instacli from.

```yaml instacli
Code example: Call another instacli file from working dir

Run script:
  file: samples/basic/create-greeting.cli
  input:
    name: Clarice

Expected output: Hello Clarice!
```

As a shortcut, you can pass the name of the script in the current working directory as a single text parameter. But in
that case, it is not possible to pass input parameters

```yaml instacli
Code example: Call another instacli file from working dir without input

Run script: samples/basic/create-greeting.cli

Expected output: Hello World!
```
