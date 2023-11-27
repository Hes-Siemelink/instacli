# Command: Script info

`Script info` contains the description of a script file.

| Content type | Supported |
|--------------|-----------|
| Value        | yes       |
| List         | no        |
| Object       | no        |

## Basic usage

With **Script info** you give a script a description.

There should be only one **Script info** command in a file, and it should be in top, so you can easily read it when opening the file.

**Script info** takes only text.

```yaml
Script info: A script containing a code example

Code example: Code example for Script info
```

When running Instacli from the command line with the `cli` command, this is the description that is given.

