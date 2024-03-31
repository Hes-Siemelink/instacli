# Command: DELETE

`DELETE` sends a DELETE request to an HTTP endpoint.

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

Just specify the endpoint to send the **DELETE** request to.

```yaml instacli
Code example: Simple DELETE

DELETE: http://localhost:2525/items
```

or use the longer form if you need to specify more details

```yaml instacli
Code example: DELETE with more properties

DELETE:
  url: http://localhost:2525/items
  username: admin
  password: admin
```

## Http request defaults

As with all Http commands, you can use [Http request defaults](Http request defaults.md) to set the defaults for common
fields.

```yaml instacli
Code example: Http request defaults and DELETE

Http request defaults:
  url: http://localhost:2525

DELETE: /items
```

See [Http request defaults](Http request defaults.md) for more information on how to configure all fields.