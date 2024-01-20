# Command: Get accounts

Gets all accounts from the user's account list.

| Content type | Supported |
|--------------|-----------|
| Value        | yes       |
| List         | implicit  |
| Object       | no        |

## Basic usage

With **Get accounts** you get the list of connection details for a certain endpoint.

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

You can retrieve all accounts with the following snippet:

```yaml
Code example: Get default account for an endpoint

Get accounts: Instacli Sample Server

Expected output:
  - name: Test account 1
    username: admin
    password: admin
  - name: Test account 2
    username: user
    password: user
```