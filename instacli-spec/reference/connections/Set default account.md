# Command: Set default account

Sets the default account for the user's account list..

| Content type | Supported                |
|--------------|--------------------------|
| Value        | no                       |
| List         | implicit                 |
| Object       | yes                      |
| `target`     | The name of the endpoint |
| `name`       | The name of the account  |

## Basic usage

Use **Set default account** to set another account that will be used for [Get account](Get%20account.md) when there are
multiple.

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

You can set the default account with the following snippet:

```yaml script
Code example: Set the default account for an endpoint

Set default account:
  target: Instacli Sample Server
  name: Test account 2
```