# Command: Delete account

Deletes an account from the user's account list.

| Content type | Supported                |
|--------------|--------------------------|
| Value        | no                       |
| List         | implicit                 |
| Object       | yes                      |
| `target`     | The name of the endpoint |
| `name`       | The name of the account  |

## Basic usage

With **Delete account** you get rid of a previously configured account.

Given the following connections in `~/.instacli/connections.yaml`:

```yaml file:connections.yaml
Instacli Sample Server:
  accounts:
    - name: Test account 1
      username: admin
      password: admin
    - name: Test account 2
      username: user
      password: user
```

You can delete Test account 2 with the following snippet:

```yaml script
Code example: Delete an account for an endpoint

Delete account:
  target: Instacli Sample Server
  name: Test account 2
```