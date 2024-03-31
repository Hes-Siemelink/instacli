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

```yaml instacli
Code example: Simple POST

POST:
  url: http://localhost:2525/items
  body:
    1: One
    2: Two
    3: Three
```

## Http request defaults

As with all Http commands, you can use [Http request defaults](Http request defaults.md) to set the defaults for common
fields.

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

See [Http request defaults](Http request defaults.md) for more information on how to configure all fields.