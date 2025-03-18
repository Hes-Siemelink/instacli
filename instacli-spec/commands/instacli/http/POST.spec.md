# Command: POST

`POST` sends a POST request to an HTTP endpoint.

| Content type | Supported |
|--------------|-----------|
| Value        | no        |
| List         | implicit  |
| Object       | yes       |

[POST.schema.yaml](schema/POST.schema.yaml)

## Basic usage

Specify `url` and `body` to send a **POST** request.

```yaml instacli
Code example: Simple POST

POST:
  url: http://localhost:2525/items
  body:
    1: One
    2: Two
    3: Three
```

## Post without body

You can use the shortcut notation to send a POST request without a body.

```yaml instacli
Code example: POST without body

POST: http://localhost:2525/echo/body

Expected output: { }
```

## Http request defaults

As with all Http commands, you can use [Http request defaults](Http%20request%20defaults.spec.md) to set the defaults
for common fields.

```yaml instacli
Code example: Http request defaults and POST

Http request defaults:
  url: http://localhost:2525

POST:
  path: /items
  body:
    1: One
    2: Two
    3: Three
```

See [Http request defaults](Http%20request%20defaults.spec.md) for more information on how to configure all fields.
