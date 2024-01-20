# Command: POST

`POST` sends a POST request to an HTTP endpoint.

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

Specify `url` and `body` to send a **POST** request.

```yaml
Code example: Simple POST

POST:
  url: http://localhost:25125/items
  body:
    1: One
    2: Two
    3: Three
```

## Http request defaults

As with all Http commands, you can use [Http request defaults](Http%20request%20defaults.md) to set the defaults for
common fields.

```yaml
Code example: Http request defaults and POST

Http request defaults:
  url: http://localhost:25125

POST:
  path: /items
  body:
    1: One
    2: Two
    3: Three
```

See [Http request defaults](Http%20request%20defaults.md) for more information on how to configure all fields.
