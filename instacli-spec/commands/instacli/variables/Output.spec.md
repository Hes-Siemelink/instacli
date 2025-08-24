# Command: Output

`Output` sets the `${output}` variable, that can be the output of a script

| Content type | Supported |
|--------------|-----------|
| Value        | yes       |
| List         | yes       |
| Object       | yes       |

[Output.schema.yaml](schema/Output.schema.yaml)

## Basic usage

Instacli assigns the result of a command to the `${ouput}` variable

```yaml instacli
Code example: Output variable is automatically set

Replace:
  text: me
  in: Hello me
  with: World!

# Prints Hello World!
Print: ${output}

# Checks output variable
Expected output: Hello World!
```

With **Output**, you explicitly set the the `${output}` variable.

```yaml instacli
Code example: Set the output variable expicitly

Output: Hello World!

Print: ${output}
Expected output: Hello World!
```

You can put any kind of data in Output

```yaml instacli
Code example: Output variable with object content

Output:
  name: John
  age: 10

Expected output:
  name: John
  age: 10
```

It is a shorthand to using the variable assignment syntax, but easier to type

```yaml instacli
Code example: Set output variable with variable syntax

${output}: Hello World!

Expected output: Hello World!
```

## Output of a script

After the script is run, the `${output}` variable is passed to the caller. You would typically have an **Output**
statement at the end of your script to make explicit what the result fo the script is.

Here's an example of a script that defines the input and then makes it clear what the output is.

```yaml instacli
Code example: Define input and output in a script

Input parameters:
  name:
    description: Person to greet
    default: Nobody

Output: Hello ${input.name}!
```

## Output in For each

Use **Output** with [For each](../control-flow/For%20each.spec.md) to transform a list:

```yaml instacli
Code example: Transform a list with For each and Output


For each:
  ${name} in:
    - Alice
    - Bob
  Output: Hello ${name}!

Expected output:
  - Hello Alice!
  - Hello Bob! 
```