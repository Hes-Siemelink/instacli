# Command: Get all credentials

Gets all credentials for an endpoint.

| Content type | Supported |
|--------------|-----------|
| Value        | yes       |
| List         | implicit  |
| Object       | no        |

## Basic usage

With **Get all credentials** you get the list of connection details for a certain endpoint.

Given the following list of credentials in `~/.instacli/credentials.yaml`:

```yaml file:credentials.yaml
Instacli Sample Server:
  credentials:
    - name: Test account 1
      username: admin
      password: admin
    - name: Test account 2
      username: user
      password: user
```

You can retrieve all credentials with the following snippet:

```yaml instacli
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