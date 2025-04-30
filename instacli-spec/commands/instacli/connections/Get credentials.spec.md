# Command: Get credentials

Gets the default credentials for an endpoint.

| Content type | Supported |
|--------------|-----------|
| Value        | yes       |
| List         | implicit  |
| Object       | no        |

[Get credentials.schema.yaml](schema/Get%20credentials.schema.yaml)

## Basic usage

With **Get credentials** you get the default connection details for a certain endpoint.

Given the following list of credentials in `~/.instacli/credentials.yaml`:

```yaml file=credentials.yaml
Instacli Sample Server:
  credentials:
    - name: Test account
      username: admin
      password: admin
```

<!-- yaml instacli
Credentials: ${SCRIPT_TEMP_DIR}/credentials.yaml
-->

You can retrieve the default credentials with the following snippet:

```yaml instacli
Code example: Get default credentials for an endpoint

Get credentials: Instacli Sample Server

Expected output:
  name: Test account
  username: admin
  password: admin
```

