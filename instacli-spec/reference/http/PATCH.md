# Command: PATCH

`PATCH` sends a PATCH request to an HTTP endpoint.

| Content type | Supported                         |
|--------------|-----------------------------------|
| Value        | no                                |
| List         | implicit                          |
| Object       | yes                               |
| url          | base URL of the request           |
| path         | path of the request               |
| body         | request body                      |
| username     | username for Basic authentication |
| password     | password for Basic authentication |
| headers      | request headers                   |
| saveAs       | save the result to a file         |

## Basic usage

Specify `url` and `body` to send a **PATCH** request.

```yaml cli
Code example: Simple PATCH

PATCH:
  url: http://localhost:2525/items
  body:
    item: one
```

## Http request defaults

As with all Http commands, you can use [Http request defaults](Http%20request%20defaults.md) to set the defaults for
common fields.

```yaml cli
Code example: Http request defaults and PATCH

Http request defaults:
  url: http://localhost:2525

PATCH:
  path: /items
  body:
    item: one
```

See [Http request defaults](Http%20request%20defaults.md) for more information on how to configure all fields.
