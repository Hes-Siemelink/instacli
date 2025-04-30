# Command: Set default credentials

Sets the default credentials for an endpoint.

| Content type | Supported |
|--------------|-----------|
| Value        | no        |
| List         | implicit  |
| Object       | yes       |

[Set default credentials.schema.yaml](schema/Set%20default%20credentials.schema.yaml)

## Basic usage

Use **Set default credentials** to set the defeult credentials that will be used
for [Get credentials](Get%20credentials.spec.md) when there are multiple available.

Given the following connections in `~/.instacli/credentials.yaml`:

```yaml file=credentials.yaml
Instacli Sample Server:
  credentials:
    - name: Test account 1
      username: admin
      password: admin
    - name: Test account 2
      username: user
      password: user
```

<!-- yaml instacli
Credentials: ${SCRIPT_TEMP_DIR}/credentials.yaml
-->


You can set the default credentials with the following snippet:

```yaml instacli
Code example: Set the default credentials for an endpoint

Set default credentials:
  target: Instacli Sample Server
  name: Test account 2
```