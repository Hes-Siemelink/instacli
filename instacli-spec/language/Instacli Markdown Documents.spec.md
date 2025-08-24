# Instacli Markdown Documents

If by now you are getting accustomed to the fact that Instacli is Yaml, get ready for the kicker: Instacli scripts can
also be written in Markdown!

This way, we can freely mix code, data _and_ documentation in a single file. Another way to llok at it is that in the
documentation, you embed the code and data for your script. This style is
called [literate programming](https://en.wikipedia.org/wiki/Literate_programming): telling the story of what you want to
do, while at the same time defining the executable code that defines how to do it.

This style is particularly useful for specification, where you want to describe the desired behavior, show the code and
the output and automatically test that they are correct.

Another use case are script files, where the source reads like a README file explaining what is going on.

Here is an example of a Markdown file `hello.cli.md` that contains Instacli code:

~~~markdown
### My Instacli script

This is a simple Instacli script that prints a greeting.

```yaml instacli
Print: Hello world!
```
~~~

Your document will look like

> ### My Instacli script
>
>    This is a simple Instacli script that prints a greeting.
>
>    ```yaml instacli
>    Print: Hello world!
>    ```

And when you run it, Instacli will execute the code in the `yaml instacli` block and print the output:

```shell ignore
cli hello
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
code block, and then how they will show up in the document.

# Writing Instacli Markdown

To write Instacli Markdown, just write a Markdown document like you would do normally. Instacli will pick up certain
code blocks to execute.

Here is an overview of the constructs you can use to embed Instacli code in Markdown documents.

| Description                                           | Markdown directive    |
|-------------------------------------------------------|-----------------------|
| [Instacli code](#instacli-code)                       | ` ```yaml instacli`   | 
| [Hidden code](#hidden-code)                           | ` <!-- yaml instacli` | 
| [Predefined answers](#predefined-answers)             | ` <!-- answers`       | 
| [Checking output](#checking-output)                   | ` ```output`          | 
| [Helper files](#helper-files)                         | ` ```yaml file`       | 
| [Shell commands](#shell-commands)                     | ` ```shell`           | 
| [Invoking Instacli itself](#invoking-instacli-itself) | ` ```shell cli`       | 

## Instacli code

Define Instacli code with the ` ```yaml instacli` markdown block directive.

#### Markdown format

~~~markdown
Here is an Instacli code example:

```yaml instacli
Code example: An Instacli snippet inside Markdown

Print: Hello from Instacli!
```
~~~

#### Display example

Here is an Instacli code example:

```yaml instacli
Code example: An Instacli snippet inside Markdown

Print: Hello from Instacli!
```

## Hidden code

Sometimes a code example gets cluttered with setup code.

Consider this interactive example. In order for the automated tests to run, we need to provide an answer, so the test
will not hang on input:

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

#### Markdown format

~~~markdown
A cleaner looking example:

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

#### Display example

A cleaner looking example:

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

#### Markdown format

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

#### Display example

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

For checking the output of a command within a script, use the
[**Expected output**](../commands/instacli/testing/Expected%20output.spec.md) command inside the script. Note: there is
no Markdown shortcut for **Expected output**.

#### Markdown format

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

#### Display example

The following snippet

```yaml instacli
Code example: Example with output check

Print: Hello, Alice!
```

should produce the following output:

```output
Hello, Alice!
```

## Printing to the console

When writing an Instacli script you can use the `>` quote character to print to the console, as a shortcut for using the
[**Print**](../commands/instacli/util/Print.spec.md) command.


<!-- yaml instacli
Code example: "Print with > character"
-->

#### Markdown format

~~~markdown
While running a script, you can print to the console with the `>` character:

> Script is running

So that should be in the output:

```output
Script is running
```
~~~

#### Display example

While running a script, you can print to the console with the `>` character:

> Script is running

So that should be in the output:

```output
Script is running
```

## Helper files

If you need to have a helper file for the example to work, you can define one with ` ```yaml
file=[filename]`. This is a shortcut for the [**Temp file**](../commands/instacli/files/Temp%20file.spec.md)
command.

The file will be created in the temporary directory of the script. You can use the `${SCRIPT_TEMP_DIR}` variable to
refer to this directory

#### Markdown format

~~~markdown
You can define the data in an external file `data.yaml`:

```yaml file=data.yaml
key: value
```

And then read it with **Read file**:

```yaml instacli
Code example: Read from a file

Read file: ${SCRIPT_TEMP_DIR}/data.yaml

Expected output:
    key: value
```
~~~

#### Display example

You can define the data in an external file `data.yaml`:

```yaml file=data.yaml
key: value
```

And then read it with **Read file**:

```yaml instacli
Code example: Read from a file

Read file: ${SCRIPT_TEMP_DIR}/data.yaml

Expected output:
  key: value
```

### Variables inside temp files

When using the ` ```yaml file` directive, the contents are stored as-is and variables inside the file are not resolved.

If you need dynamic content with variables and eval blocks, use the `resolve=[boolean]` option.

This is equivalent to using the
[**Temp file**](../commands/instacli/files/Temp%20file.spec.md) command with the `resolve` option. Note that for the
` ```yaml file` directive, the default is `false`, where is for **Temp file** the default is `true`.

#### Markdown format

~~~markdown
Suppose you have defined a variable inside a Yaml script:

```yaml instacli
${value}: large
```

You can use the variable value by when creating a file `config.yaml` by specifying `resolve=true`:

```yaml file=config.yaml resolve=true
size: ${value}
```

When we read it, we will see that the value was resolved:

```yaml instacli
Read file: ${SCRIPT_TEMP_DIR}/config.yaml

Expected output:
    size: large
```
~~~

#### Display example

Suppose you have defined a variable inside a Yaml script:

```yaml instacli
${value}: large
```

You can use the variable value by when creating a file `config.yaml` by specifying `resolve=true`:

```yaml file=config.yaml resolve=true
size: ${value}
```

When we read it, we will see that the value was resolved:

```yaml instacli
Read file: ${SCRIPT_TEMP_DIR}/config.yaml

Expected output:
  size: large
```

## Shell commands

To execute a command in the shell, use the ` ```shell` directive.

Instacli will execute this command using the [**Shell**](../commands/instacli/shell/Shell.spec.md) command.

The output of the shell command can be checked with the ` ```ouput` directive.

#### Markdown format

~~~markdown
Execute the shell command:

```shell
echo Hello
```

And check the output:

```output
Hello
```
~~~

#### Display example

Execute the shell command:

```shell
echo Hello
```

And check the output:

```output
Hello
```

#### Yaml equivalent

The above example is equivalent to the following Instacli script in Yaml:

```yaml instacli
Code example: Shell command with output check in Instacli

Shell:
  command: echo Hello
  show output: true

Expected console output: Hello
```

### Setting the current directory

Set the current directory with the `cd` option. This is equivalent to using the
[**Shell**](../commands/instacli/shell/Shell.spec.md) command with the `cd` option.

The following example shows how to set the current directory to the temporary directory created by Instacli for the
execution of the current script. This is where temporary files are stored that are created with ` ```yaml file` or
`Temp file`.

#### Markdown format

~~~markdown
The following snippet shows the contents of the file we created previously:

```shell cd=${SCRIPT_TEMP_DIR}
cat data.yaml
```

The output should be:

```output
key: value
```
~~~

#### Display example

The following snippet shows the contents of the file we created previously:

```shell cd=${SCRIPT_TEMP_DIR}
cat data.yaml
```

The output should be:

```output
key: value
```

### Options to show command and output

There are two options to show the command and output of the shell command.

`show_command=[boolean]` will show the command that is executed. The default is `false`. This is equivalent to using the
[**Shell**](../commands/instacli/shell/Shell.spec.md#displaying-the-shell-command) command with the `show command`
option.

`show_output=[boolean]` will show and record the output of the command. The default is `true`. This is equivalent to
using the
[**Shell**](../commands/instacli/shell/Shell.spec.md#displaying-the-output) command with the `show output` option. Note:
the default for **Shell** is `false`.

#### Markdown format

~~~markdown
Show shell command but not the output:

```shell show_command=true show_output=false
ls /tmp
```

The output should be:

```output
ls /tmp
```
~~~

#### Display example

Show shell command but not the output:

```shell show_command=true show_output=false
ls /tmp
```

The output should be:

```output
ls /tmp
```

### Shell ignore

When you want to use the shell directive for display purposes only, you can use the `ignore` option. This will prevent
the command from being executed.

This way, tools like GitHub will show it as a shell command, but it will not be executed by Instacli.

#### Markdown format

~~~markdown
Instacli will not execute this command:

```shell ignore
kill 1
```
~~~

#### Display example

Instacli will not execute this command:

```shell ignore
kill 1
```

## Invoking Instacli itself

You can also use the ` ```shell cli` directive to show how to invoke Instacli itself. This is equivalent to using the
[**Cli**](../commands/instacli/shell/Cli.spec.md) command.

This is useful for showing how to use the `cli` command and its command line options.

Note: Within an Instacli script itself, there are better ways to invoke another script.
See [Calling another script](Organizing%20Instacli%20files%20in%20directories.spec.md#calling-another-instacli-script).

#### Markdown format

~~~markdown
Show `cli` usage with the `--help` option:

```shell cli
cli --help
```

The output should be:

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

#### Display example

Show `cli` usage with the `--help` option:

```shell cli
cli --help
```

The output should be:

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

### Setting the current directory

Like ` ```shell`, you can set the current directory with the `cd` option. This is equivalent to using the
[**Cli**](../commands/instacli/shell/Cli.spec.md) command with the `cd` option.

The following example shows how to set the current directory to the temporary directory created by Instacli for the
execution of the current script. This is where temporary files are stored that are created with ` ```yaml file`.

#### Markdown format

~~~markdown
Create a file `hello.cli`:

```yaml file=hello.cli
Print: Hello world!
```

And then run it:

```shell cli cd=${SCRIPT_TEMP_DIR}
cli hello
```

The output should be:

```output
Hello world!
```
~~~

#### Display example

Create a file `hello.cli`:

```yaml file=hello.cli
Print: Hello world!
```

And then run it:

```shell cli cd=${SCRIPT_TEMP_DIR}
cli hello
```

The output should be:

```output
Hello world!
```

### Ignore option

You can also use `ignore` on `shell cli`. This willl not trigger execution.

In the following example, `cli unknown-command` would raise an error, but this command is never executed by Instacli so
we can continue safely.

<!-- yaml instacli
Code example: Ignore shell command
-->

#### Markdown format

~~~markdown
Instacli will not execute this command:

  ```shell cli ignore
  cli unknown-command
```

So there should be no output:

```output
```
~~~

#### Display example

Instacli will not execute this command:

```shell cli ignore
cli unknown-command
```

So there should be no output:

```output
```
