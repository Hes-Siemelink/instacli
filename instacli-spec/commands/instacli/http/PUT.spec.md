# Command: PUT

`PUT` sends a PUT request to an HTTP endpoint.

| Content type | Supported |
|--------------|-----------|
| Value        | no        |
| List         | implicit  |
| Object       | yes       |

[PUT.schema.yaml](schema/PUT.schema.yaml)

## Basic usage

Specify `url` and `body` to send a **PUT** request.

```yaml instacli
Code example: Simple PUT

PUT:
  url: http://localhost:2525/items
  body:
    1: One
    2: Two
    3: Three
```

## Http request defaults

As with all Http commands, you can use [Http request defaults](Http%20request%20defaults.spec.md) to set the defaults
for common fields.

```yaml instacli
Code example: Http request defaults and PUT

Http request defaults:
  url: http://localhost:2525

PUT:
  path: /items
  body:
    1: One
    2: Two
    3: Three
```

See [Http request defaults](Http%20request%20defaults.spec.md) for more information on how to configure all fields.
