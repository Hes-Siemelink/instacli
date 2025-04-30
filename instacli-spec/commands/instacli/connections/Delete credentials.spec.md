# Command: Delete credentials

Deletes credentials for an endpoint.

| Content type | Supported |
|--------------|-----------|
| Value        | no        |
| List         | implicit  |
| Object       | yes       |

[Delete credentials.schema.yaml](schema/Delete%20credentials.schema.yaml)

## Basic usage

With **Delete credentials** you get rid of previously configured credentials.

Given the following list of credentials in `~/.instacli/credentials.yaml`:

```yaml file=credentials.yaml
Instacli Sample Server:
  credentials:
    - name: Test account 1
      username: admin
      password: admin
    - name: Test account 2
      username: user
      password: user
```

<!-- yaml instacli
Credentials: ${SCRIPT_TEMP_DIR}/credentials.yaml
-->


You can delete Test account 2 with the following snippet:

```yaml instacli
Code example: Delete credentials for an endpoint

Delete credentials:
  target: Instacli Sample Server
  name: Test account 2
```