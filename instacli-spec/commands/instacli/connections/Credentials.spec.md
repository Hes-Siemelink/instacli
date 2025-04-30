# Command: Credentials

Use a different credentials file than the default `~/.instacli/credentials.yaml`.

| Content type | Supported |
|--------------|-----------|
| Value        | yes       |
| List         | implicit  |
| Object       | no        |

[Credentials.schema.yaml](schema/Credentials.schema.yaml)

## Basic usage

By default, credentials are stored in the `~/.instacli/credentials.yaml` file. You can use the `Credentials` command to
specify a different file.

Given the following file `my_credentials.yaml` in the `SCRIPT_TEMP_DIR` directory:

```yaml file=my_credentials.yaml
My Sample Server:
  credentials:
    - name: My account
      username: admin
      password: admin
```

We can use the `Credentials` command to specify this file

```yaml instacli
Code example: Use a different credentials file

Credentials: ${SCRIPT_TEMP_DIR}/my_credentials.yaml

Get credentials: My Sample Server

Expected output:
  name: My account
  username: admin
  password: admin
```

## Specify credentials inline

The **credentials** command takes a file name as a parameter. Here is an example of how to specify credentials inline
using the [Temp file](../files/Temp%20file.spec.md) command and
the [Eval syntax](../../../language/Eval%20syntax.spec.md):

```yaml instacli
Code example: Specify credentials inline

Credentials:
  :Temp file:
    content:
      My Temp Server:
        credentials:
          - name: My account
            username: admin
            password: admin

Get credentials: My Temp Server

Expected output:
  name: My account
  username: admin
  password: admin
```
