# Command: Create credentials

Configures credentials for an endpoint and saves it in the user's preferences.

| Content type     | Supported |
|------------------|-----------|
| Value            | no        |
| List             | implicit  |
| Object           | yes       |
| target           | String    |
| credentials      | Object    |
| credentials.name | String    |

## Basic usage

With **Create credentials** you can store user credentials to connect to an endpoint. Retrieve them with
the [Get credentials](Get%credentials.md) command.

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

The account data is stored in the `~/.instacli/credentials.yaml` file:

```yaml file:credentials.yaml
Instacli Sample Server:
  credentials:
    - name: Test account
      username: admin
      password: admin
```