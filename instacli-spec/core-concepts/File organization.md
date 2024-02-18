# Instacli File Organization

## What does a single Instacli script look like

### Hello world example

This is the simplest Instacli file:

```yaml file:hello-world.cli
Print: Hello world
```

Instacli files are a list of commands. In this case the file contains a single `Print` command. By convention, commands
start with a capital letter.

If we store it in a file `hello-world.cli` we can run it with the following command:

```commandline cli
cli hello-world.cli
```

And get the expected output:

```cli output
Hello world
```

### It's all YAML

Instacli files are just Yaml.

<!-- Note: avoid 'wall of text' here and move the motivational part somewhere else -->

Instacli files have `.cli` file extension. Within Instacli, the convention is that `.yaml` files are for static data
and `.cli` files are Instacli scripts that contain commands.. So don't write your scripts with a `.yaml` extension.
Instacli relies on the `.cli` extension in some cases, like when calling an Instacli script form another Instacli
script. Map your editor to recognize `.cli`
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

Instacli's Yaml flavor is 'disguised JSON' and does not rely on special Yaml features like directives.

Instacli adds some additional formatting on top of Yaml, for example the variable syntax `${..}`.

### Multiple commands and `---`

You can have multiple commands in a file and they will be executed in order:

```yaml file:prompt.cli
Prompt: What is your name?

Print: Hello ${output}!
```

When storing this in a file `prompt.cli` and running it, would print something like

<!-- cli input
What is your name?: Hes
-->

```commandline cli
cli prompt.cli
```

```cli output
? What is your name? Hes
Hello Hes!
```

The commands in a file are just dictionary keys. We run into trouble if we want to use the same command again:

This won't work because it's invalid Yaml.

```yaml
Print: Hello
Print: Hello again!  # Invalid YAML!
```

The solution is to add the YAML document separator `---`:

```yaml script
Print: Hello
---
Print: Hello again!
```

It's not very pretty, but it works.

There are some ways to avoid the `---` separator,

With most commands you can simply supply the arguments as a list, and they will be executed in sequence:

```yaml script
Print:
  - Hello
  - Hello again!
```

<!-- Print is a bad example!!! It is a list processor designed to print lists "as lists" -->

Another approach is using the `Do` command, that takes a list of command

```yaml script
Do:
  - Print: Hello
  - Print: Hello again!
```

You could argue that an Instacli script should have a giant `Do` on top, and all commands should be specified in the
list. You can do that, but it looks distracting to me, so I prefer the separator approach. Instacli scripts should be
short & sweet, and the separator helps you to divide sections in your code.

### Script info

You can specify the description of your script with the **[Script info](../reference/script-definition/Script%20info.md)
** command.

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

You can set the actual output of the script with the **[Output](../reference/variables/Output.md)** command.

For example, with script `hello.cli`

```yaml file:hello.cli
Output:
  a: one
  b: two
  c: three
```

The output is stored in the `${output}` variable. When invoking instacli with the `-o` option it will be printed:

```commandline cli
cli -o hello.cli
```

```cli output
a: one
b: two
c: three
```

You also get it when calling a script from another script:

```yaml script
Run script:
  relative: hello.cli

Expected output:
  a: one
  b: two
  c: three
```

## Organizing multiple Instacli files in directories

### Calling another Instacli script

### The `.instacli.yaml` file

### Using `cli` command on a directory
