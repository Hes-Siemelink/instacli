# Command: Set default credentials

Sets the default credentials for an endpoint.

| Content type | Supported                |
|--------------|--------------------------|
| Value        | no                       |
| List         | implicit                 |
| Object       | yes                      |
| `target`     | The name of the endpoint |
| `name`       | The name of the account  |

## Basic usage

Use **Set default credentials** to set the defeult credentials that will be used
for [Get credentials](Get credentials.md) when there are multiple available.

Given the following connections in `~/.instacli/credentials.yaml`:

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

You can set the default credentials with the following snippet:

```yaml instacli
Code example: Set the default credentials for an endpoint

Set default credentials:
  target: Instacli Sample Server
  name: Test account 2
```