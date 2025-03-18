# Command: PATCH

`PATCH` sends a PATCH request to an HTTP endpoint.

| Content type | Supported |
|--------------|-----------|
| Value        | no        |
| List         | implicit  |
| Object       | yes       |

[PATCH.schema.yaml](schema/PATCH.schema.yaml)

## Basic usage

Specify `url` and `body` to send a **PATCH** request.

```yaml instacli
Code example: Simple PATCH

PATCH:
  url: http://localhost:2525/items
  body:
    item: one
```

## Http request defaults

As with all Http commands, you can use [Http request defaults](Http%20request%20defaults.spec.md) to set the defaults
for common fields.

```yaml instacli
Code example: Http request defaults and PATCH

Http request defaults:
  url: http://localhost:2525

PATCH:
  path: /items
  body:
    item: one
```

See [Http request defaults](Http%20request%20defaults.spec.md) for more information on how to configure all fields.
