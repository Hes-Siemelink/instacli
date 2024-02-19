# Command: Run script

Use **Run script** to run another Instacli script. See also [Instacli files as commands](Instacli files as commands.md)

| Content type | Supported                                                |
|--------------|----------------------------------------------------------|
| Value        | no                                                       |
| List         | implicit                                                 |
| Object       | yes                                                      |
| `file`       | the Instacli file to run                                 |
| *            | Any other field than `file` will be passed as a variable |

## Basic usage

Suppose you have a cli file `generate-greeting.cli`

```yaml file:generate-greeting.cli
Output: Hello ${input.name}!
```

Then you can call it from another Instacli file using **Run script**.

```yaml instacli
Code example: Call another instacli file

Run script:
  relative: generate-greeting.cli
  input:
    name: Alice

Expected output: Hello Alice!
```

When you specify a field, it will be passed as part of the `${input}` variable in the script that you call. For example,
the value for `name`, Alice, will be available as `${input.name}` in the target script.   
