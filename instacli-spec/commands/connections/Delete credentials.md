# Command: Delete credentials

Deletes credentials for an endpoint.

| Content type | Supported                |
|--------------|--------------------------|
| Value        | no                       |
| List         | implicit                 |
| Object       | yes                      |
| `target`     | The name of the endpoint |
| `name`       | The name of the account  |

## Basic usage

With **Delete credentials** you get rid of previously configured credentials.

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

You can delete Test account 2 with the following snippet:

```yaml instacli
Code example: Delete credentials for an endpoint

Delete credentials:
  target: Instacli Sample Server
  name: Test account 2
```