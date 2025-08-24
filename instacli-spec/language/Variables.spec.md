# Variables

## Basic usage

Instacli variables are written in `${..}` syntax.

Assign a variable using `${var}:` followed by some content.

You can then use the variable in any context.

```yaml instacli
Code example: Simple variable usage

${var}: Hello

Print: ${var}
```

This will print:

```script output
Hello
```

<!-- TODO add support for 'script output' in tests -->

## Path reference

If a variable contains a nested object structure, you can use JavaScript-like path notation to retrieve the contents.
For example, `${book.chapters[0].pages}` is a valid way to access the number of pages in the first chapter in the
following example:

```yaml instacli
Code example: Variable path notation

${book}:
  title: A Small History
  chapters:
    - title: Introduction
      pages: 6
    - title: Main Story
      pages: 13

Assert equals:
  - actual: ${book.title}
    expected: A Small History
  - actual: ${book.chapters[0].pages}
    expected: 6
```

💡Note: this is _not_ JsonPath

## The ${output} variable

The result of each command is stored in the variable `${output}`.

```yaml instacli
Code example: The output variable

Add:
  - 1
  - 1

Assert equals:
  - actual: ${output}
    expected: 2
```

Since we know that there will always an output variable, there is some special support for it in Instacli. For example,
we can rewrite the above example as

```yaml instacli
Code example: The output variable

Add:
  - 1
  - 1

Expected output: 2
```

This uses the built-in **[Expected output](../commands/instacli/testing/Expected%20output.spec.md)** command that makes
the script more readable.

The output variable behaves as any variable. You can also set it:

```yaml instacli
Code example: Setting the output variable directly

${output}: Hello

Expected output: Hello
```

In fact, there is another shortcut for that: the **[Output](../commands/instacli/variables/Output.spec.md)** command.

```yaml instacli
Code example: Setting the output variable with 'Output'

Output: Hello

Expected output: Hello
```

Commands that don't have a result will not affect the output` variable.

```yaml instacli
Code example: Print doesn't clear the output variable

Output: Hello

Print: something else

Expected output: Hello
```

## Capturing output

If you want to do something with the output variable, it is dangerous to use it directly because any subsequent command
may change it.

Use the **[As](../commands/instacli/variables/As.spec.md)** command to capture the output in another variable.

```yaml instacli
Code example: Store output in another variable

Add:
  - 1
  - 1
As: ${sum}

Assert equals:
  actual: ${sum}
  expected: 2
```

## Script output

The **Output** command is often used at the end of a script to explicitly set the return value.

Suppose you have a file `simple-greeting.cli`

```yaml file=simple-greeting.cli
Script info: A simple greeting

Output: Hello World!
```

If we call this script from another script, we get the result in the`${output}` variable:

```yaml instacli
Code example: Get the output of another script

Run script: simple-greeting.cli

Expected output: Hello World!
```

## The input variable

The input of a script defined in the **Input parameters** section will be stored in the `${input}` variable

```yaml instacli
Code example: Populating the input variable

Script info: Creates a greeting

Input parameters:
  name:
    description: Your name
    default: World

Assert equals:
  actual: ${input}
  expected:
    name: World
```

## Script Input & Output

Take a look at the example file `greet.cli` to see how you can define input and output of a script.

```yaml file=greet.cli
Code example: Input and output when defining a script

Script info: Creates a greeting

Input parameters:
  name:
    description: Your name

Output: Hello ${input.name}!
```

Now you can call it with input and retrieve the output. In the following example we use the name of the script as a
command. (See **[Instacli files as commands](../commands/instacli/files/Instacli%20files%20as%20commands.spec.md)**)

```yaml instacli
Code example: Input and output when calling a script

Greet:
  name: Alice

Expected output: Hello Alice!
```