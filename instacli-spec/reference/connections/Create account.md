# Command: Create account

Configures a connection to an endpoint for a user account and saves it in the user's preferences.

| Content type | Supported |
|--------------|-----------|
| Value        | no        |
| List         | implicit  |
| Object       | yes       |
| target       | String    |
| account      | Object    |
| account.name | String    |

## Basic usage

With **Create account** you can store user credentials to connect to an endpoint. Retrieve them with
the [Get account](Get%20account.md) command.

```yaml cli
Code example: Create an account

Create account:
  target: Instacli Sample Server
  account:
    name: Test Account
    username: admin
    password: admin
```

The endpoint is identified by the `target` parameter. You can put arbitrary data in the `account` section, but it should
have a `name` field to identify it,

The account data is stored in the `~/.instacli/connections.yaml` file:

```yaml file:connections.yaml
Instacli Sample Server:
  accounts:
    - name: Test account
      username: admin
      password: admin
```