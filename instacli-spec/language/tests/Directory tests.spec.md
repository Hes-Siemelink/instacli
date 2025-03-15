# Directory tests

This contains Instacli behavior that is too boring for the main spec but should be tested.

## Empty directory

This is what happens when you run `cli` in an empty directory.

The `.instacli.yaml` file for the `empty` directory:

```commandline
```yaml file:empty/.instacli.yaml
Script info: This is an example directory
```

There are no scripts in the `empty` directory.

```commandline cli
cli --help empty
```

Will say:

```output
This is an example directory

No commands available.
```

