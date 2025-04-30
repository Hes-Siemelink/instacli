# Directory tests

This contains Instacli behavior that is too boring for the main spec but should be tested.

## Empty directory

This is what happens when you run `cli` in an empty directory.

The `.instacli.yaml` file for the `empty` directory:

```yaml file=empty/.instacli.yaml
Script info: This is an example directory
```

There are no scripts in the `empty` directory.

```shell cli
cli --help empty
```

Will say:

```output
This is an example directory

No commands available.
```

## Imported helper scripts

Suppose you have a main script `main.cli` in the directory `main`:

```yaml file=main/main.cli
Script info: Main script

Say something: { }
```

And a helper script `say-something.cli` in the `helper` directory:

```yaml file=helper/helper.cli
Output: Hello
```

You can import the say-something script by way of the `.instacli.yaml` file in the `main` directory:

```yaml file=main/.instacli.yaml
Script info: Main directory
imports:
  - ../helper/say-something.cli
```

Then the `helper` script will be available in the main directory, but will not show up when printing the contents

```shell cli
cli --help main
```

```output
Main directory

Available commands:
  main   Main script
```
