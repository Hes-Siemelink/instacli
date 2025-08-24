## Organizing Instacli files in directories

With one or more files in a directory, you can run the directory as a cli command. The Instacli scripts will be
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

We can now run `basic` as a cli command with subcommands for each of the files. First, let's use the `--help` option to
see some more descriptions

```shell cli cd=samples
cli --help basic
```

```output
Simple Instacli example scripts

Available commands:
  create-greeting   Creates a greeting and puts it in the output
  greet             Prints a greeting
  multiple-choice   Interaction example
  output            Sets test output
  prompt            Simple interactive prompt
```

We can now invoke the **greet** command like this:

```shell cli cd=samples
cli basic greet
```

With the expected output:

```output
Hello, World!
```

Note that it's optional to specify the `.cli` extension. The following three commands are equivalent:

```shell cli cd=samples
cli basic greet
```

```shell cli cd=samples
cli basic greet.cli
```

```shell cli cd=samples
cli basic/greet.cli
```

### Interactive command chooser

When invoking a directory without the `--help` parameter, Instacli lets you select the command with an interactive
prompt. This is a great way to explore the commands and subcommands!

<!-- Insert gif here -->

```shell ignore
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

### Calling another Instacli script

We showed above that you can call another Instacli script from within an Instacli script with the
**[Run script](../commands/instacli/files/Run%20script.spec.md)** command.

Another way is to use it as a regular command. Instacli reads all cli files in the same directory and makes them
available as commands in the current script. While doing so, it transforms file names in "kebab-style" to "Sentence
style".

For example, suppose we have a file `create-greeting.cli`, that creates a greeting and puts it in the output:

```yaml file=create-greeting.cli
Script info: Creates a greeting

Input parameters:
  name: Your name

Output: Hello ${name}!
```

We can now call it as `Create greeting` from a script in the same directory:

```yaml instacli
Code example: Calling another cli file

Create greeting:
  name: Cray

Expected output: Hello Cray!
```

## The .instacli.yaml file

Each directory can have a `.instacli.yaml` file that contains metadata about the directory.

You can give the directory a readable description, import cli files from other directories and manage http connection
data.

### Directory description

Add a `.instacli.yaml` file to the directory to give a description to the current directory.

```yaml file=.instacli.yaml
Script info: This is an example directory
```

The information is printed when displaying help for the directory:

```shell cli
cli --help .
```

```output
This is an example directory

Available commands:
  create-greeting   Creates a greeting
```

If there is no `.instacli.yaml` file, or it doesn't have a description, Instacli will use the first sentence of the
README.md file in the directory.
<!-- TODO: Add example and test cases -->

### Hidden directory

You can hide the directory from the interactive command chooser by setting the `hidden` property to `true`.

For example take the following `.instacli.yaml` file in the `subcommand` directory:

```yaml file=subcommand/.instacli.yaml
Script info:
  hiddent: true
```

It will not show up as a subcommand when invoking `cli --help`.

### Instacli version

You can indicate the version of the Instacli spec that the script is using.

```yaml instacli
Script info:
  instacli-spec: v0.1
```

### Importing files from another directory

Out-of-the-box, you
can [call a script from within the same directory](Organizing%20Instacli%20files%20in%20directories.spec.md#calling-another-instacli-script)
as a regular Instacli command.

To call a script from another directory, you can import it in the `.instacli.yaml` file. This will import it for all
scripts in that directory.

For example, if we have the file `helper/say-something.cli`:

```yaml file=helper/say-something.cli
Output: Something ${input.what}
```

And we have it in the `.instacli.yaml` file as follows:

```yaml file=.instacli.yaml
Script info: This is an example directory

imports:
  - helper/say-something.cli
```

Then you can call it like this from your script `call-helper.cli`:

```yaml file=call-helper.cli
Code example: Calling a script that was imported from another directory

Say something:
  what: funny

Expected output: Something funny
```

Run it

```shell cli
cli -o call-helper
```

And it will output:

```output
Something funny
```

### Specifying connection data

The `.instacli.yaml` file also contains a `connections` settings for retrieving HTTP connection credentials. See the
**[Connect to](../commands/instacli/connections/Connect%20to.spec.md)** command for more details.


