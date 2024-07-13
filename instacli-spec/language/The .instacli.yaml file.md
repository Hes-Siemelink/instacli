# The .instacli.yaml file

Each directory can have a `.instacli.yaml` file that contains metadata about the directory.

You can give the directory a readable description, import cli files from other directories and manage http connection
data.

## Directory description

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
  say-something   Say something
```

## Importing files from another directory

Out-of-the-box, you
can [call a script from within the same directory](Anatomy%20of%20Instacli%20files.md#calling-another-instacli-script)
as a regular Instacli command.

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

## Specifying connection data

The `.instacli.yaml` file also contains a `connections` settings for retrieving HTTP connection credentials. See the
**[Connect to](../commands/instacli/connections/Connect%20to.md)** command for more details.

<!--
TODO: Explain hidden commands
-->