# Command: Create credentials

Configures credentials for an endpoint and saves it in the user's preferences.

| Content type | Supported |
|--------------|-----------|
| Value        | no        |
| List         | implicit  |
| Object       | yes       |

[Create credentials.schema.yaml](schema/Create%20credentials.schema.yaml)

## Basic usage

With **Create credentials** you can store user credentials to connect to an endpoint. Retrieve them with
the [Get credentials](Get%20credentials.spec.md) command.

<!-- yaml instacli
Credentials: ${SCRIPT_TEMP_DIR}/credentials.yaml
-->

```yaml instacli
Code example: Create credentials for an endpoint

Create credentials:
  target: Instacli Sample Server
  credentials:
    name: Test Account
    username: admin
    password: admin
```

The endpoint is identified by the `target` parameter. You can put arbitrary data in the `credentials` section, but it
should have a `name` field to identify it,

The account data is stored in the `~/.instacli/credentials.yaml` file like this:

```yaml
Instacli Sample Server:
  credentials:
    - name: Test account
      username: admin
      password: admin
```