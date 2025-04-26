# Instacli Markdown Documents

If by now you are getting accustomed to the fact that Instacli is Yaml, get ready for the kicker: Instacli scripts can
also be written in Markdown!

This way, we can freely mix code, data _and documentation_ in a single file. This is "literate programming": telling the
story of what you want to do, while at the same time defining the executable code. This style is particularly useful for
documentation, where you want to show the code and the output at the same time. Another use case are script files that
can be read as manual pages.

Here is an example of a Markdown file `hello.cli.md` that contains Instacli code:

    # My Instacli script
    
    This is a simple Instacli script that prints a greeting.
    
    ```yaml instacli
    Print: Hello world!
    ```

Your document will look like

> # My Instacli script
>
>    This is a simple Instacli script that prints a greeting.
>
>    ```yaml instacli
>    Print: Hello world!
>    ```

And when you run it, Instacli will execute the code in the `yaml instacli` block and print the output:

```shell ignore
cli hello.cli.md
```

```
Hello world!
```

## Specifying Instacli in Instacli

The Instacli specification itself is written in Instacli Markdown. All the documents are run as unit tests in the
Instacli build, validating the code examples and test cases. This way we can make sure that the implementation of the
specification is always correct!

Note that all the examples in _this_ document are also tested automatically. In order for that to work, you will see
examples twice: first we will show how they are written in Markdown, and then how they will show up in the document. The
Instacli build will pick up the second form and test it.

# Writing Instacli Markdown

Here is an overview of the constructs you can use to embed Instacli code in Markdown documents.

## Instacli code

You can define Instacli code with the ` ```yaml instacli` markdown block directive.

### Markdown format

    ```yaml instacli
    Code example: An Instacli snippet inside Markdown
    
    Print: Hello from Instacli!
    ```

### Display example

```yaml instacli
Code example: An Instacli snippet inside Markdown

Print: Hello from Instacli!
```

## Hidden code

Sometimes a code example could become can become cluttered with setup code.

For example, consider an interactive example. In order for the automated tests to run, we need to provide a stock
answer:

```yaml instacli
Code example: Example with setup code

Stock answers:
  What is your name?: Alice

Prompt: What is your name?
As: ${name}

Print: Hello, ${name}!
```

When reading the example, a reader may be distracted by the **Stock answers** bit. That is not what this code example is
about. It would be great if could hide it in someway.

You can do so by putting the code that we need but don't want to show in an HTML comment starting with
`<!-- yaml instacli`

### Markdown format

    <!-- yaml instacli
    Stock answers:
      What is your name?: Alice
    -->

    ```yaml instacli
    Code example: Example without setup code

    Prompt: What is your name?
    As: ${name}
    
    Print: Hello, ${name}!
    ```

### Display example

Now the example looks a lot cleaner:

    <!-- yaml instacli
    Stock answers:
      What is your name?: Alice
    -->

```yaml instacli
Code example: Example without setup code

Prompt: What is your name?
As: ${name}

Print: Hello, ${name}!
```

You can also provide hidden cleanup code with `<!-- yaml instacli` when putting it after the code example that is being
displayed. The yaml code will be appended to script.

## Answers

When writing documentation or tests, you may want to provide answers to questions that are asked in the code. You can do
so by embedding the **[Stock answers](../commands/instacli/testing/Stock%20answers.spec.md)** command in a hidden code
block. A more concise way of doing this is to use the `<!-- answers` HTML comment.

### Markdown format

    <!-- answers
    What is your name?: Alice
    -->

    ```yaml instacli
    Code example: Example with answers block

    Prompt: What is your name?
    As: ${name}
    
    Print: Hello, ${name}!
    ```

### Display example

You won't notice the difference on how it is being displayed, but the markdown is a bit cleaner:

<!-- answers
What is your name?: Alice
-->

```yaml instacli
Code example: Example with answers block

Prompt: What is your name?
As: ${name}

Print: Hello, ${name}!
```

## Checking output

You can check the console output of a code block using the ` ```output` directive. This is equivalent to using the
command [Expected console output](../commands/instacli/testing/Expected%20console%20output.spec.md). If the output of
the command is not the same as specified in the ` ````output` block, the script will fail.

Note: for checking the output of a command within a script, use
the [Expected output](../commands/instacli/testing/Expected%20output.spec.md) command inside the script. There is no
Markdown shortcut for that

### Markdown format

    The following snippet

    ```yaml instacli
    Code example: Example with output check

    Print: Hello, Alice!
    ```

    should produce the following output:

    ```output
    Hello, Alice!
    ```

### Display example

The following snippet

```yaml instacli
Code example: Example with output check

Print: Hello, Alice!
```

should produce the following output:

```output
Hello, Alice!
```

## Files

Sometimes you need to have a helper file in order for the example to work. You can define a helper file with **```yaml
file:[filename]**. This is a shortcut for the [Temp file](../commands/instacli/tempfile/Temp%20file.spec.md) command.

### Markdown format

    You can define the data in an external file `data.yaml`:

    ```yaml file:data.yaml
    key: value
    ```
    
    And then read it with **Read file**:

    ```yaml instacli
    Code example: Read from a file

    Read file:
        resource: data.yaml
    As: ${data}
    
    Print: Value is ${data.key}

### Display example

You can define the data in an external file `data.yaml`:

```yaml file:data.yaml
key: value
```

And then read it with **Read file**:

```yaml instacli
Code example: Read from a file

Read file:
  resource: data.yaml
As: ${data}

Print: Value is ${data.key} 
```

## Shell commands

When you have an inline command line example, use the ` ```shell` directive.

### Markdown format

Write the shell command in a code block:

    ```shell
    echo Hello
    ```

Instacli will execute this command using the [Shell](../commands/instacli/shell/Shell.spec.md) command. In other words,
this is equivalent to:

    ```yaml instacli
    Shell: echo Hello
    ```

You can also provide the output of the command in a block:

    ```output
    Hello
    ```

### Display example

This will show as

```shell
echo Hello
```

with output

```output
Hello
```

### Shell ignore

When you want to use the shell directive for display purposes only, you can use the `ignore` option. This will prevent
the command from being executed.

Using the shell directive this way, tools like GitHub will show it as a command shell command, but it will not be
executed by Instacli.

### Markdown format

Write the shell command in a code block with `ignore`:

    ```shell ignore
    kill 1
    ```

Instacli will not execute this command.

### Display example

This will show as

```shell ignore
kill 1
```

and will not provide output.

## Invoking Instacli itself with the `cli` command

### Markdown format

To show how to invoke the command line `cli` command, use the following syntax:

    ```shell cli
    cli --help
    ```

Then followed by a block that contains the output:

    ```output
    Instacli -- Instantly create CLI applications with light scripting!
    
    Usage:
       cli [options] file | directory
    
    Options:
    ```

### Display example

This will show as:

```shell cli
cli --help
```

Followed by:

```output
Instacli -- Instantly create CLI applications with light scripting!

Usage:
   cli [global options] file | directory [command options]

Global options:
  --help, -h          Print help on a script or directory and does not run anything
  --output, -o        Print the output at the end of the script in Yaml format
  --output-json, -j   Print the output at the end of the script in Json format
  --non-interactive, -q   Indicate that Instacli should not prompt for user input
  --debug, -d         Run in debug mode. Prints stacktraces when an error occurs.
```

### Ignore option

You can also use `ignore` on `shell cli`

    ```shell cli ignore
    cli unknown-command
    ```

This will show as:

```shell cli ignore
cli unknown-command
```

But will not trigger execution. In this case, `cli unknown-command` whould raise and error, but this command is never
executed in the script. 