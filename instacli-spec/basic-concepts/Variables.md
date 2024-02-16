# Variables

## Basic usage

Instacli variables are written in `${..}` syntax.

```yaml script
Code example: Simple variable usage

${myvar}: Hello

Print: ${myvar}
```

This will print:

```script output
Hello
```

<!-- TODO add support for 'script output' in tests -->

Assign a variable using `${var}:` followed by some content.

You can then use the variable in any content.

## Path reference

If a variable contains a nested object structure, you can use JavaScript-like path notation to retrieve the contents.

```yaml script
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

The result of each command is stored in the  `${output}` variable.

```yaml script
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

```yaml script
Code example: The output variable

Add:
  - 1
  - 1

Expected output: 2
```

This uses the built-in **[Expected output](../reference/testing/Expected%20output.md)** command that makes the script
more readable.

The `${output}` variable behaves as any variable. You can also set it:

```yaml script
Code example: Setting the output variable directly

${output}: Hello

Expected output: Hello
```

In fact, there is another shortcut for that: the **[Output](../reference/variables/Output.md)** command.

```yaml script
Code example: Setting the output variable with 'Output'

Output: Hello

Expected output: Hello
```

Commands that don't have a result will not affect the `${output}` variable.

```yaml script
Code example: Print doesn't clear the output variable

Output: Hello

Print: something else

Expected output: Hello
```

## Capturing output

If you want to do something with the output variable, it is dangerous to use it directly because it will change after
each command that returns something. Use the **[As](../reference/variables/As.md)** command to store the output variable
in another one.

```yaml script
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

The **Output** command is often used at the end of a script to indicate the return value.

Suppose you have a file `simple-greeting.cli`

```yaml file:simple-greeting.cli
Script info: A simple greeting

Output: Hello World!
```

If we call this script from another script, we get the result in the`${output}` variable:

```yaml script
Code example: Get the output of another script

Run script:
  file: simple-greeting.cli

Expected output: Hello World!
```

## The input variable

The input defined in the **Script info** section will be stored in the `${input}` variable

```yaml script
Code example: Populating the input variable

Script info:
  description: Creates a greeting
  input:
    name:
      description: Your name
      default: World

Assert equals:
  actual: ${input}
  expected:
    name: World
```