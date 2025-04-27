# Instacli Markdown Documents

If by now you are getting accustomed to the fact that Instacli is Yaml, get ready for the kicker: Instacli scripts can
also be written in Markdown!

This way, we can freely mix code, data _and_ documentation in a single file. Another way to llok at it is that in the
documentation, you embed the code and data for your script. This style is
called [literate programming](https://en.wikipedia.org/wiki/Literate_programming): telling the story of what you want to
do, while at the same time defining the executable code that defines how to do it.

This style is particularly useful for specification, where you want to describe the desired behavior, show the code and
the output and automatically test that they are correct. Another use case are script files, where the source reads like
a README file explaining what is going on.

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

## The Instacli specification is in Instacli!

The Instacli specification itself is written in Instacli Markdown. All the documents in these directories are run as
unit tests in the Instacli build, validating the code examples and test cases. This way we can make sure that the
implementation of the specification is always up-to-date and correct!

That means that all the examples in _this_ document are also tested automatically. But we also need to describe them. In
order for that to work, you will see examples twice: first we will show how they are written in Markdown as a literal
code block, and then how they will show up in the document. The Instacli build will pick up the second form and test it.

# Writing Instacli Markdown

To write Instacli Markdown, just write a Markdown document like you would do normally. Instacli will pick up certain
code blocks to execute.

Here is an overview of the constructs you can use to embed Instacli code in Markdown documents.

| Markdown directive    | Description                                                                |
|-----------------------|----------------------------------------------------------------------------|
| ` ```yaml instacli`   | [Instacli code](#instacli-code)                                            | 
| ` <!-- yaml instacli` | [Hidden code](#hidden-code)                                                | 
| ` <!-- answers`       | [Predefined answers](#answers)                                             | 
| ` ```output`          | [Checking output](#checking-output)                                        | 
| ` ```yaml file`       | [Files](#files)                                                            | 
| ` ```shell`           | [Shell commands](#shell-commands)                                          | 
| ` ```shell cli`       | [Invoking Instacli itself](#invoking-instacli-itself-with-the-cli-command) | 

## Instacli code

Define Instacli code with the ` ```yaml instacli` markdown block directive.

### Markdown format

~~~markdown
Here is an Instacli code example:

```yaml instacli
Code example: An Instacli snippet inside Markdown

Print: Hello from Instacli!
```
~~~

### Display example

Here is an Instacli code example:

```yaml instacli
Code example: An Instacli snippet inside Markdown

Print: Hello from Instacli!
```

## Hidden code

Sometimes a code example could become can become cluttered with setup code.

For example, consider an interactive example. In order for the automated tests to run, we need to provide an answer, so
the test will not hang on input:

```yaml instacli
Code example: Example with setup code

Answers:
  What is your name?: Alice

Prompt: What is your name?
As: ${name}

Print: Hello, ${name}!
```

When reading the example, a reader may be distracted by the **Answers** bit. That is not what this code example is
about. It would be great if we could hide it somehow.

The way to do this is to put the code that we don't want to show in an HTML comment starting with
`<!-- yaml instacli`.

### Markdown format

~~~markdown
<!-- yaml instacli
Answers:
  What is your name?: Alice
-->

```yaml instacli
Code example: Example without setup code

Prompt: What is your name?
As: ${name}

Print: Hello, ${name}!
```
~~~

### Display example

Now the example looks a lot cleaner:

<!-- yaml instacli
Answers:
  What is your name?: Alice
-->

```yaml instacli
Code example: Example without setup code

Prompt: What is your name?
As: ${name}

Print: Hello, ${name}!
```

### Cleanup code

You can also use `<!-- yaml instacli` to provide hidden cleanup code by putting a code block after the code example that
is displayed. The yaml code from the comment will be appended to script.

## Predefined answers

When writing documentation or tests, you may want to provide answers to questions that are asked in the code. You can do
so by embedding the [**Answers**](../commands/instacli/testing/Answers.spec.md) command in a hidden code block. A more
concise way of doing this is to use the `<!-- answers` HTML comment.

### Markdown format

~~~markdown
<!-- answers
What is your name?: Alice
-->

```yaml instacli
Code example: Example with answers block

Prompt: What is your name?
As: ${name}

Print: Hello, ${name}!
```
~~~

### Display example

You won't notice the difference on how it is being displayed, but the markdown is a bit cleaner.

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
command [**Expected console output**](../commands/instacli/testing/Expected%20console%20output.spec.md). If the output
of the command is not the same as specified in the ` ````output` block, the script will fail.

For checking the output of a command within a script, use the [**Expected output
**](../commands/instacli/testing/Expected%20output.spec.md) command inside the script. (Note that there is no Markdown
shortcut for that.)

### Markdown format

~~~markdown
The following snippet

```yaml instacli
Code example: Example with output check

Print: Hello, Alice!
```

should produce the following output:

```output
Hello, Alice!
```
~~~

### Display example

The following snippet

```yaml instacli
Code example: Example with output check

Print: Hello, Alice!
```

should show the following output on the console:

```output
Hello, Alice!
```

## Files

Sometimes you need to have a helper file in order for the example to work. You can define a helper file with ` ```yaml
file:[filename]`. This is a shortcut for the [**Temp file**](../commands/instacli/tempfile/Temp%20file.spec.md)
command.

### Markdown format

~~~markdown
You can define the data in an external file `data.yaml`:

```yaml file:data.yaml
key: value
```

And then read it with **Read file**:

```yaml instacli
Code example: Read from a file

Read file:
    resource: data.yaml

Expected output:
    key: value
```
~~~

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

Expected output:
  key: value
```

### Variables inside temp files

When using the ` ```yaml file` directive, the contents are stored as-is and variables inside the file are not resolved.
If you need dynamic content with variables and eval blocks, use [**Temp file
**](../commands/instacli/tempfile/Temp%20file.spec.md) inside a script.

## Shell commands

To execute a command in the shell, use the ` ```shell` directive.

Instacli will execute this command using the [**Shell**](../commands/instacli/shell/Shell.spec.md) command.

The output of the shell command can be chacked with the ` ```ouput` directive.

### Markdown format

Write the shell command in a code block:

~~~markdown
```shell
echo Hello
```
~~~

And check the output with

~~~markdown
```output
Hello
```
~~~

### Display example

This will show as

```shell
echo Hello
```

with output check

```output
Hello
```

### Instacli equivalent

The above example is equivalent to the following Instacli code:

```yaml instacli
Code example: Shell command with output check in Instacli

Shell:
  command: echo Hello
  show output: true

Expected console output: Hello
```

### Shell ignore

When you want to use the shell directive for display purposes only, you can use the `ignore` option. This will prevent
the command from being executed.

Using the shell directive this way, tools like GitHub will show it as a command shell command, but it will not be
executed by Instacli.

### Markdown format

Write the shell command in a code block with `ignore`:

~~~markdown
```shell ignore
kill 1
```
~~~

Instacli will not execute this command.

### Display example

This will show as

```shell ignore
kill 1
```

and will not provide output.

## Invoking Instacli itself

You can also use the ` ```shell cli` directive to show how to invoke Instacli itself. This is equivalent to using the
[**Cli**](../commands/instacli/shell/Cli.spec.md) command.

This is useful for showing how to use the `cli` command from the shell and use the command line options.

Within an Instacli script itself, there are better ways to invoke another script.
See [Calling another script](Organizing%20Instacli%20files%20in%20directories.spec.md/#calling-another-instacli-script).

### Markdown format

To show how to invoke the command line `cli` command, use the following syntax:

~~~markdown
```shell cli
cli --help
```
~~~

Then followed by a block that contains the output:

~~~markdown
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
~~~

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