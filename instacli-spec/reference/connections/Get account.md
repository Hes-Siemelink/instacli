# Command: Get account

Gets the default account from the user's account list.

| Content type | Supported |
|--------------|-----------|
| Value        | yes       |
| List         | implicit  |
| Object       | no        |

## Basic usage

With **Get account** you get the connection details for a certain endpoint.

Given the following connections in `~/.instacli/connections.yaml`:

```yaml file:connections.yaml
Instacli Sample Server:
  accounts:
    - name: Test account
      username: admin
      password: admin
```

You can retrieve the default account with the following snippet:

```yaml cli
Code example: Get default account for an endpoint

Get account: Instacli Sample Server

Expected output:
  name: Test account
  username: admin
  password: admin
```

