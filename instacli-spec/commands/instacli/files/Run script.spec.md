# Command: Run script

Use **Run script** to run another Instacli script. See
also [Instacli files as commands](Instacli%20files%20as%20commands.spec.md)

| Content type | Supported |
|--------------|-----------|
| Value        | no        |
| List         | implicit  |
| Object       | yes       |

[Run script.schema.yaml](schema/Run%20script.schema.yaml)

## Basic usage

Suppose you have a cli file `do-something.cli`

```yaml file=do-something.cli
Output: Something done!
```

Then you can call it from another Instacli file using **Run script**.

Pass the name of the script in the current working directory as a single text parameter.

```yaml instacli
Code example: Call another instacli

Run script: do-something.cli

Expected output: Something done!
```

## Passing input parameters

Suppose you have a cli file `create-greeting.cli`

```yaml file=create-greeting.cli
Output: Hello ${input.name}!
```

To call it with **Run script**, you can pass the input parameters in the `input` property. The script name is passed in
the `resource` property.

```yaml instacli
Code example: Call another instacli file

Run script:
  resource: create-greeting.cli
  input:
    name: Alice
    default: World

Expected output: Hello Alice!
```

## Finding the script

In the example above, we used the property `resource` to indicate that the script to be called was in the same directory
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

