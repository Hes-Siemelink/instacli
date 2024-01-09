# Command: Out

`Out` sets the `${out}` variable, that can be the output of a script

| Content type | Supported |
|--------------|-----------|
| Value        | yes       |
| List         | yes       |
| Object       | yes       |

## Basic usage

Instacli assigns the result of a command to the `${out}` variable

```yaml
Code example: Output variable is automatically set

Replace:
  in: Hello me
  find: me
  replace with: World!

# Prints Hello World!
Print: ${out}

# Checks output variable
Expected output: Hello World!
```

With **Out** you explicitly set the `${out}` variable.

```yaml
Code example: Set the output variable expicitly

Out: Hello World!

Print: ${out}
Expected output: Hello World!
```

You can put any kind of data in `${out}`

```yaml
Code example: Set the 'out' variable with object content

Out:
  name: John
  age: 10

Expected output:
  name: John
  age: 10
```

It is a shorthand to using the variable assignment syntax, but a little easier to type or spot in a file

```yaml
Code example: Set output variable with variable syntax

${out}:
  name: John
  age: 10

Expected output:
  name: John
  age: 10
```

## Output of a script

When defining a script, you would typically put  [Input](Input.md) at the top, and **Out** at the bottom. This way
you define the contract of the script in a
way that is easy to read.

```yaml
Code example: Define input and output in a script

Input:
  name:
    description: Person to greet
    default: Nobody

Out: Hello ${name}!
```

## Output in For each

Use **Out** with [For each](../control-flow/For%20each.md) to transform a list:

```yaml
Code example: Transform a list with For each and Output


For each:
  ${name} in:
    - Alice
    - Bob
  Out: Hello ${name}!

Expected output:
  - Hello Alice!
  - Hello Bob! 
```