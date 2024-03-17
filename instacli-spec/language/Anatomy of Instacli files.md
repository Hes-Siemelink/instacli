# Anatomy of Instacli files

## What does a single Instacli script look like

### Hello world example

This is the simplest Instacli file:

```yaml file:hello-world.cli
Print: Hello world
```

Instacli files are a list of **commands**. In this case the file contains a single `Print` command. By convention,
commands start with a capital letter.

If we store it in a file `hello-world.cli`, we can run it with the following command:

```commandline cli
cli hello-world.cli
```

And get the expected output:

```cli output
Hello world
```

### It's all YAML

Instacli files are just Yaml.

Instacli files have `.cli` file extension. Within Instacli, the convention is that `.yaml` files are for static data
and `.cli` files are Instacli scripts that contain commands. Tip: Map your editor to recognize `.cli`
as `.yaml`.

Instacli files being Yaml has its pros and cons.

The main idea is to blur the line between 'code' and 'data'. By having the 'data' closer to 'code' it makes it easier to
adopt a declarative style: saying _what_ you want to happen rather than _how_ you want it to be carried out. In this
style, you would have a higher 'data content' than 'logic content' in your scripts. Ideally, more than 50% of the script
is 'data', and the 'logic' is a smaller part. Then Yaml starts making sense as a format.

By convention, commands start with a capital letter, for example `Print` or `Read file`, to distinguish them from data.

Another advantage of Instacli syntax being YAML is predictability: if you have worked with Yaml before you know its
structure and pitfalls already, and you get editor support out-of-the-box. It's also way easier for the implementation
to parse it than a custom format.

Instacli does add some additional formatting on top of Yaml, for example the variable syntax `${..}`.

Instacli's Yaml flavor does not rely on special Yaml features like directives. It is used as a human-friendly way of
writing JSON.

### Multiple commands and `---`

You can have multiple commands in a file and they will be executed in order:

```yaml file:prompt.cli
Prompt: What is your name?

Print: Hello ${output}!
```

When storing this in a file `prompt.cli` and running it with the following command,

<!-- cli input
What is your name?: Hes
-->

```commandline cli
cli prompt.cli
```

you would get the following:

```cli output
? What is your name? Hes
Hello Hes!
```

The commands in a file are just dictionary keys. We run into trouble if we want to use the same key again.

For example, this won't work because it's invalid Yaml:

```yaml
Print: Hello
Print: Hello again!  # Invalid YAML!
```

The solution is to add the YAML document separator `---`:

```yaml instacli
Print: Hello
---
Print: Hello again!
```

It's not very pretty, but it works. This is the best option in most cases.

There are some ways to avoid the `---` separator, though.

With most commands you can simply supply the arguments as a list, and they will be executed in sequence:

```yaml instacli
Print:
  - Hello
  - Hello again!
```

<!-- Print is a bad example!!! It is a list processor designed to print lists "as lists" -->

Another approach is using the `Do` command, that takes a list of command

```yaml instacli
Do:
  - Print: Hello
  - Print: Hello again!
```

You could argue that an Instacli script should have a giant `Do` on top, and all commands should be specified in the
list. That's perfectly fine code, but to me it looks distracting, so I prefer the separator approach. Besides, the
separator helps you to visually divide sections in your code if placed strategically.

### Script info

You can specify the description of your script with the
**[Script info](../commands/script-info/Script%20info.md)** command.

Take this file `simple-greeting.cli`:

```yaml file:simple-greeting.cli
Script info: Prints a simple greeting

Print: Hi there!
```

When running it with the `--help` flag, the description is printed:

```commandline cli
cli --help simple-greeting.cli
```

```cli output
Prints a simple greeting
```

### Defining script input

**Script info** is also used to specify input parameters. Here's `greet.cli`:

```yaml file:greeting.cli
Script info:
  description: Prints a personalized greeting
  input:
    name: Your name

Print: Hello ${input.name}!
```

It has input parameter `description`:

```commandline cli
cli --help greeting.cli
```

```cli output
Prints a personalized greeting

Options:
  --name   Your name
```

And you can invoke it as follows.

```commandline cli
cli greeting.cli --name Bob
```

```cli output
Hello Bob!
```

### Script output

You can set the actual output of the script with the **[Output](../commands/variables/Output.md)** command.

For example, with script `hello.cli`

```yaml file:hello.cli
Output:
  a: one
  b: two
  c: three
```

The output is stored in the `${output}` variable. When invoking instacli with the
**[--output](Command%20line%20options.md#--output)** option, it will be printed:

```commandline cli
cli --output hello.cli
```

```cli output
a: one
b: two
c: three
```

You also get it when calling a script from another script:

```yaml instacli
Run script:
  resource: hello.cli

Expected output:
  a: one
  b: two
  c: three
```

## Organizing multiple Instacli files in directories

With multiple files in a directory, you can run the directory as a cli command. The Instacli scripts will be
subcommands.

For this example we run from the **[samples](/samples)** directory. It contains a directory `basic` with the following
files:

```
create-greeting.cli
greet.cli
greeting.yaml
multiple-choice.cli
output.cli
simple-question.cli
```

We can now run **basic** as a cli command with subcommands for each of the files. First, let's use the `--help` option
to see some more descriptions

```commandline cli directory:samples
cli --help basic
```

```cli output
Simple Instacli example scripts

Available commands:
  create-greeting   Creates a greeting and puts it in the output
  greet             Prints a greeting
  multiple-choice   Interaction example
  output            Sets test output
  prompt            Simple interactive prompt
```

We can now invoke the **greet** command like this:

```commandline cli directory:samples
cli basic greet
```

With the expected output:

```cli output
Hello, World!
```

Note that it's optional to specify the `.cli` extension. The following three commands are equivalent:

```commandline cli directory:samples
cli basic greet
```

```commandline cli directory:samples
cli basic greet.cli
```

```commandline cli directory:samples
cli basic/greet.cli
```

### Interactive command chooser

When invoking a directory without the `--help` parameter, Instacli lets you select the command with an interactive
prompt. This is a great way to explore the commands and subcommands!

<!-- Insert gif here -->

```commandline
cli basic       
```

```
Simple Instacli example scripts

* Available commands: 
 > create-greeting          Creates a greeting and puts it in the output
   greet                    Prints a greeting
   output                   Sets test output
   prompt-multiple-choice   Interaction example
   prompt-simple-question   Simple interactive prompt
```

## The .instacli.yaml file

Each directory can have a `.instacli.yaml` file that contains metadata about the directory.

You can give the directory a readable description, import cli files from other directories and manage http connection
data.

### Calling another Instacli script

We showed above the you can call another Instacli script from within an Instacli script with the
**[Run script](../commands/files/Run%20script.md)** command.

Another way is to use it as a regualr command. Instacli reads all cli files in the same directory and makes them
available as commands in the current script. While doing so, it transforms file names in "kebab-style" to "Sentence
style".

For example, suppose we have a file `create-greeting.cli`, that creates a greeting and puts it in the output:

```yaml file:create-greeting.cli
Script info:
  description: Creates a greeting
  input:
    name: Your name

Output: Hello ${input.name}!
```

We can now call it as `Create greeting` from a script in the same directory:

```yaml instacli
Code example: Calling another cli file

Create greeting:
  name: Cray

Expected output: Hello Cray!
```

### Directory description

Add a `.instacli.yaml` file to the directory to give a description to the current directory.

```yaml
Script info: This is an example directory
```

The information is printed when displaying help for the directory:

```commandline cli
cli --help .
```

```cli output
This is an example directory

Available commands:
  create-greeting   Creates a greeting
  greeting          Prints a personalized greeting
  hello             Hello
  hello-world       Hello world
  prompt            Prompt
  say-something     Say something
  simple-greeting   Prints a simple greeting
```

### Importing files from another directory

As shown [above](#calling-another-instacli-script), you can call a script from within the same directory like it was a
regular Instacli command.

To call a script from another directory, you can import it in the `.instacli.yaml` file. This will import it for all
scripts in that directory.

For example, if we have the file `helper/say-something.cli`:

```yaml file:say-something.cli
Output: Something ${input.what}
```

And we have it in the `.instacli.yaml` file as follows:

```yaml file:.instacli.yaml
Script info: This is an example directory

imports:
  - helper/say-something.cli
```

Then you can call it like this from your script:

```yaml instacli
Code example: Calling a script that was imported from another directory

Say something:
  what: funny

Expected output: Something funny
```

### Specifying connection data

The `.instacli.yaml` file also contains a `connections` settings for retrieving HTTP connection credentials. See the
**[Connect to](../commands/connections/Connect%20to.md)** command for more details.

<!--
Explain hidden commands
-->