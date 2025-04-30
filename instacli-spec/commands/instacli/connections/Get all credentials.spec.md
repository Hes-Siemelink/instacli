# Command: Get all credentials

Gets all credentials for an endpoint.

| Content type | Supported |
|--------------|-----------|
| Value        | yes       |
| List         | implicit  |
| Object       | no        |

[Get all credentials.schema.yaml](schema/Get%20all%20credentials.schema.yaml)

## Basic usage

With **Get all credentials** you get the list of connection details for a certain endpoint.

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


You can retrieve all credentials with the following snippet:

```yaml FIXME instacli -- there was a hack in TestUtil to use mock credentials specified above but now it doesn't work anymore
Code example: Get all credentials for an endpoint

Get all credentials: Instacli Sample Server

Expected output:
  - name: Test account 1
    username: admin
    password: admin
  - name: Test account 2
    username: user
    password: user
```